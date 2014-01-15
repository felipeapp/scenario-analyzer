<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>

	
<h:form id="formulario">

	<h2><ufrn:subSistema /> > Certificados para Avaliadores </h2>

	<div class="infoAltRem">				    
		<h:graphicImage value="/img/certificate.png" height="19" width="19" style="overflow: visible;"/>: Certificado		    		    
	</div>
	
	<br />
	
	<table class="listagem" id="adHoc">
		<caption> Lista de participações como avaliador ad hoc</caption>
		<thead>
			<tr>
				<th width="50%"> Ação de extensão </th>
				<th> Data da Avaliação </th>				
				<th> Nota </th>
				<th> Parecer </th>
				<th></th>
			</tr>
		</thead>
		
		<tbody>
		
			<c:forEach items="#{certificadoAvaliadorExtensao.avaliacoesComoAdHoc}" var="avaliacao" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td> ${avaliacao.atividade.codigoTitulo}</td>
					<td> <fmt:formatDate value="${avaliacao.dataAvaliacao}" pattern="dd/MM/yyyy HH:mm:ss" /> </td>
					<td> ${avaliacao.nota} </td>
					<td> ${avaliacao.parecer.descricao}</td>
					
					<td width="2%">
						<h:commandLink
							title="Emitir certificado"
							action="#{certificadoAvaliadorExtensao.emitirCertificado}" 
							immediate="true" rendered="#{avaliacao.autorizacaoCertificado}"
							id="emitir_certificado_avaliador_">								
								<f:param name="idAvaliacao" value="#{avaliacao.id}"/>
					        	<h:graphicImage url="/img/certificate.png" height="19" width="19"/>
						</h:commandLink>
					</td>
				</tr>
				
			</c:forEach>
		
		</tbody>
	
	</table>
	
	<br />
	<br />
	
	<table class="listagem" id="membroComissao">
		<caption> Lista de participações como Membro da Comissão</caption>
		<thead>
			<tr>
				<th width="50%"> Ação de extensão </th>
				<th> Data da Avaliação </th>				
				<th> Nota </th>
				<th> Parecer </th>
				<th></th>
			</tr>
		</thead>
		
		<tbody>
		
			<c:forEach items="#{certificadoAvaliadorExtensao.avaliacoesComoMembroComissao}" var="avaliacao" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td> ${avaliacao.atividade.codigoTitulo}</td>
					<td> <fmt:formatDate value="${avaliacao.dataAvaliacao}" pattern="dd/MM/yyyy HH:mm:ss" /> </td>
					<td> ${avaliacao.nota} </td>
					<td> ${avaliacao.parecer.descricao} </td>
					<td width="2%">
						<h:commandLink
							title="Emitir certificado"
							action="#{certificadoAvaliadorExtensao.emitirCertificado}" 
							immediate="true" rendered="#{avaliacao.autorizacaoCertificado}"
							id="emitir_certificado_avaliador_comissao">								
								<f:param name="idAvaliacao" value="#{avaliacao.id}"/>
					        	<h:graphicImage url="/img/certificate.png" height="19" width="19"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		
		</tbody>
	
	</table>
				
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>