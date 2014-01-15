<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>


<f:view>
	<h2><ufrn:subSistema /> &gt; Frequ�ncias de Periodicidade dos Peri�dicos</h2>
	
	<a4j:keepAlive beanName="frequenciaPeriodicosMBean"></a4j:keepAlive>
	
	<div class="descricaoOperacao">
		<p>Caro Usu�rio,</p>
		<p>Abaixo est�o listadas as frequ�ncia de periodicidade e o tempo que expira��o para cada frequ�ncia. Esse "tempo de expira��o" � utilizado 
		para calcular quando um determinado peri�dico � considerado <strong>Corrente</strong> ou <strong>N�o Corrente</strong>. </p>
		
		<p>
			<ul>
				<li><strong>Corrente: </strong> O intervalo entre a data de registro do �ltimo fasc�culo de uma determinada assinatura e a data atual � <strong>menor</strong> que o tempo de expira��o </li>
				<li><strong>N�o Corrente: </strong> O intervalo entre a data de registro do �ltimo fasc�culo de uma determinada assinatura e a data atual � <strong>maior</strong> que o tempo de expira��o</li>
			</ul>
		</p>
	</div>
	
	<h:form id="formListaFrequenciaPeriodicos">

		<div class="infoAltRem" style="width:80%;">
			<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO} %>">
				<h:graphicImage value="/img/adicionar.gif" />
				<h:commandLink action="#{frequenciaPeriodicosMBean.preCadastrar}" value="Cadastrar Nova Periodicidade" />
			
			
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: 
				Alterar Periodicidade
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
				Remover Periodicidade
			
			</ufrn:checkRole>
		</div> 

		<table class="formulario" width="80%">
			<caption class="listagem">Lista de Frequ�ncias de Periodicidade dos Peri�dicos(${frequenciaPeriodicosMBean.size})</caption>
			<thead>
				<tr>
					<th style="width: 60%;">Descri��o</th>
					<th colspan="2" style="width: 20%; text-align: center;">Tempo de Expira��o</th>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO } %>">
						<th style="width: 3%;"></th>
						<th style="width: 3%;"></th>
					</ufrn:checkRole>
				</tr>
			</thead>
			
			<c:forEach items="#{frequenciaPeriodicosMBean.all}" var="frequencia" varStatus="status">
				
				<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					
					<td>${frequencia.descricao}</td>
					
					<td style="text-align: right;" style="width: 13%;">${frequencia.tempoExpiracaoInformadoUsuario} </td>
					
					<td style="width: 7%;">${frequencia.unidadeTempoExpiracao} </td>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO} %>">
						<td style="text-align: center;">
							<h:commandLink action="#{frequenciaPeriodicosMBean.preAtualizar}" style="border: 0;" title="Alterar Periodicidade">
								<f:param name="id" value="#{frequencia.id}" />
								<h:graphicImage url="/img/alterar.gif" alt="Alterar Periodicidade" />
							</h:commandLink>
						</td>
						<td style="text-align: center;">
							<h:commandLink action="#{frequenciaPeriodicosMBean.preRemover}" style="border: 0;" title="Remover Periodicidade" > 
								<f:param name="id" value="#{frequencia.id}" />
								<h:graphicImage url="/img/delete.gif" alt="Remover Periodicidade" />
							</h:commandLink>
						</td>
					</ufrn:checkRole>
					
				</tr>
			</c:forEach>
			
			<tfoot>
				<tr>
					<td colspan="5" style="text-align: center;">
						<h:commandButton value="Cancelar" action="#{frequenciaPeriodicosMBean.cancelar}" immediate="true"/>
					</td>
				</tr>
			</tfoot>
			
		</table>

	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>