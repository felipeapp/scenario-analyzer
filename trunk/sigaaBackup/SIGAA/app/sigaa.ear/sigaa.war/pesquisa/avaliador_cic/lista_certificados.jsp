<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> &gt; Certificados de Avaliador do CIC</h2>

<a4j:keepAlive beanName="avaliadorCIC" />
<h:form id="certificados">
	<c:if test="${empty avaliadorCIC.resultadosBusca}">
		<center><font color='red'>Você não possui certificados de avaliador disponíveis!</font></center>
	</c:if>
	
	<c:if test="${not empty avaliadorCIC.resultadosBusca}">
		<div class="descricaoOperacao">
			<p><b>Caro docente,</b></p>
			<p>Informamos que o certificado de avaliador só é disponibilizado após o período do Congresso de Iniciação Científica em questão.</p>
		</div>
		<div class="infoAltRem">
			<h:graphicImage url="/img/certificate.png" height="20" width="20" style="overflow: visible;"/> : Emitir certificado
		</div>
 		<table class="listagem">
			<caption class="listagem">Meus Certificados</caption>
			<thead>
				<tr>
					<td>Congresso</td>
					<td>Período</td>
					<td></td>
				</tr>
			</thead>
			<c:forEach items="#{avaliadorCIC.resultadosBusca}" var="item">
				<c:if test="${item.presenca == true}">
					<tr>
						<td>${item.congresso.descricao}</td>
						<td>${item.congresso.descricaoPeriodo}</td>
						
						<td width="5%">
							<h:commandLink id="link" title="Emitir certificado" action="#{avaliadorCIC.emitirCertificado}" rendered="#{item.certificadoDisponivel}">
								<f:param value="#{item.id}" name="idAvaliador"/>
			               		<h:graphicImage url="/img/certificate.png" height="20" width="20"/>
							</h:commandLink>
						</td>
					</tr>
				</c:if>
			</c:forEach>
		</table>
	</c:if>	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
