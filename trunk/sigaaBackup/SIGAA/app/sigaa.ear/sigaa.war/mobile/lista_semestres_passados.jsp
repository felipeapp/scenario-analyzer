<%@include file="/mobile/commons/cabecalho.jsp" %>

	<f:view>
			<h:form>
				<h:commandButton action="#{consultaNotasMobileMBean.voltarMenuPrincipal}" value="Voltar" /><br/>
			</h:form>
			
				<c:forEach items="#{consultaNotasMobileMBean.listaOutrosSemestres}" var="item">
				
					<h:form>										
							
							<input type="hidden" name="ano" value="${item.ano}"/>
							<input type="hidden" name="periodo" value="${item.periodo}"/>
							
							
							<h:commandButton value="#{item.ano} - #{item.periodo}" action="#{ consultaNotasMobileMBean.acessarSemestrePassado}"></h:commandButton>
					</h:form>
				</c:forEach>
	</f:view>	
<%@include file="/mobile/commons/rodape.jsp" %>
	
