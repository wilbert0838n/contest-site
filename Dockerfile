FROM gcc:latest
COPY runner.sh /runner.sh
RUN chmod +x /runner.sh
# Start the wrapper automatically
CMD ["/runner.sh"]