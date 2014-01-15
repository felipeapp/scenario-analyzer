<%@include file="/public/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<style>
	.descricaoOperacao{font-size: 1.2em;}
	h3, h4 {	font-variant: small-caps;text-align: center;	margin: 2px 0 20px;}	
	h4 { margin: 15px 0 20px; }
	.descricaoOperacao p { text-align: justify; } 
	.codVer{text-align: center;display: block;position: relative;width: 100%;font-weight: bold;}
	.vermelho{color: #FF1111;}
</style>
<f:view>

	<h:outputText value="#{solRevalidacaoDiploma.create}" />
	
	<h:form id="formViewFichaInscricao">
	

		<%-- Visualiza as informa��es rec�m cadastradas pelo interessado  --%>

			
			<h2>Comprovante de Inscri��o para <h:outputText value="#{solRevalidacaoDiploma.obj.editalRevalidacaoDiploma.titulo}"/></h2>
			
			<div class="descricaoOperacao">
				<h3> Cadastro No. <b>${solRevalidacaoDiploma.obj.id}</b> </h3>
				<c:choose>
					<%-- Visualiza as informa��es rec�m cadastradas pelo interessado  --%>
					<c:when test="${not empty solRevalidacaoDiploma.agenda && solRevalidacaoDiploma.agenda.id>0}">
						<p>
							<b style="text-transform: capitalize; ">${solRevalidacaoDiploma.obj.nome}</b>,
							<c:choose>
								<c:when test="${empty solRevalidacaoDiploma.obj.passaporte}">
									CPF  <b><ufrn:format type="cpf_cnpj" name="solRevalidacaoDiploma" property="obj.cpf"/>
								</b>
								</c:when>
								<c:otherwise>
									Passaporte  <b>${solRevalidacaoDiploma.obj.passaporte}</b>
								</c:otherwise>
							</c:choose>
		 						foi agendado para a data <b><ufrn:format type="data" valor="${solRevalidacaoDiploma.agenda.data}"/></b> 
									 e hor�rio <b>${solRevalidacaoDiploma.agenda.horario}</b> hora(s).
								
								
						</p>
						<p>
							 O interessado dever� comparecer na data e hor�rio acima, munido dos documentos necess�rios, comprovante de pagamento, e duas c�pias da ficha de inscri��o assinadas, conforme
								 descrito no <b><a class="vermelho" href="${solRevalidacaoDiploma.obj.editalRevalidacaoDiploma.urlEdital}">edital.	</a></b>
								 
						</p>
					</c:when>	
				
					<c:otherwise>
							<%-- Visualiza as informa��es dos interessados que entraram na fila  --%>
							<p>
										<b style="text-transform: capitalize; ">${solRevalidacaoDiploma.obj.nome}</b>,
										<c:choose>
											<c:when test="${empty solRevalidacaoDiploma.obj.passaporte}">
												CPF  <b><ufrn:format type="cpf_cnpj" name="solRevalidacaoDiploma" property="obj.cpf"/></b>
											</c:when>
											<c:otherwise>
												Passaporte  <b>${solRevalidacaoDiploma.obj.passaporte}</b>
											</c:otherwise>
										</c:choose>
					 						foi adicionado � lista de espera.
							</p>
							<p>
										<h4>
										No per�odo do dia <b>${solRevalidacaoDiploma.obj.editalRevalidacaoDiploma.periodoReagendamento}</b> ser� disponibilizada novas datas e hor�rios para conclus�o
									 do agendamento.
										</h4>
							</p>
					</c:otherwise>

				</c:choose>
				<center>
					<h:commandLink id="visualizarFicha"  action="#{solRevalidacaoDiploma.visualizarFicha}" target="_blank">
					 	<f:param name="idSolicitacao" value="#{solRevalidacaoDiploma.obj.id}"/>
						<b class="vermelho"> Para imprimir a ficha de inscri��o clique aqui.</b>
					</h:commandLink>
				</center>		 
				<br clear="all">
				<p>
					<small  class="codVer">
					C�digo Verificador: ${solRevalidacaoDiploma.obj.codigoHash}
					</small>
				</p>
			</div>
			
			<center>
				<a href="../home.jsf">Voltar ao Menu Principal</a>
			</center>

	</h:form>
	<br>

</f:view>

<%@include file="/public/include/rodape.jsp"%>