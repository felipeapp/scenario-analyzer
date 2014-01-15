<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

<h2><ufrn:subSistema /> &gt; Sinalizar Alunos para Depend�ncia</h2>
<a4j:keepAlive beanName="matriculaDiscenteSerieMBean"/>
<h:form id="form">

	<div class="descricaoOperacao">
		<p>Utilize esta opera��o para emitir o resultado da situa��o dos discentes de determinado curso por s�rie.</p>
		<br/>
		<ul>
			<li>Informe o ano e curso para emitir o resultado da situa��o dos discentes na s�rie com base nos aproveitamentos 
			das disciplinas dos mesmos.</li>
			<li>O aluno com uma ou mais reprova��es em disciplinas do ano ser� considerado como <b>REPROVADO</b> na s�rie. 
			Possibilitando o coordenador caracteriz�-lo como <b>APROVADO EM DEPEND�NCIA</b> na opera��o destinada a tal.</li>
			<li>Ser�o listadas apenas as s�ries com discentes REPROVADOS.</li>
			
		</ul>	 
	</div>

	<table class="formulario" width="60%">
		<caption>Dados para Sinalizar Alunos em Depend�ncia</caption>
		<tbody>
			<tr>
				<th class="obrigatorio">Curso:</th>
				<td>
					<a4j:region>
						<h:selectOneMenu value="#{matriculaDiscenteSerieMBean.serie.cursoMedio.id}" id="selectCurso"
							valueChangeListener="#{matriculaDiscenteSerieMBean.carregarSeriesByCurso }" style="width: 95%">
							<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
					 		<f:selectItems value="#{cursoMedio.allCombo}" /> 
					 		<a4j:support event="onchange" reRender="selectSerie" />
						</h:selectOneMenu>
						<a4j:status>
				                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
			            </a4j:status>
					</a4j:region>
				</td>
			</tr>
			<tr>
				<th>S�rie:</th>
				<td>
					<h:selectOneMenu value="#{ matriculaDiscenteSerieMBean.serie.id }" style="width: 95%;" id="selectSerie">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{ matriculaDiscenteSerieMBean.series }" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Ano: </th>
				<td>
					<h:inputText value="#{ matriculaDiscenteSerieMBean.ano }" onkeyup="return formatarInteiro(this);" title="Ano" maxlength="4" size="4"/>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Buscar" id="confirmar" action="#{ matriculaDiscenteSerieMBean.buscar }"/>
					<h:commandButton value="Cancelar" action="#{ matriculaDiscenteSerieMBean.cancelar }" immediate="true"/>
				</td>
			</tr>
		</tfoot>
	</table>
	
	<br/><br/>
	
	<c:if test="${not empty matriculaDiscenteSerieMBean.listaSerie}">	
	
	<div class="infoAltRem" style="width:70%">
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar S�rie
	</div>
	
	<table class="listagem" style="width:70%">
	  <caption>S�rie(s) Encontrada(s) (${fn:length(matriculaDiscenteSerieMBean.listaSerie)})</caption>
		<thead>
			<tr>
				<th style="text-align: center;" width="30%">Ano</th>
				<th>S�rie</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
		   <c:forEach var="linha" items="#{matriculaDiscenteSerieMBean.listaSerie}" varStatus="status">
		   		<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td style="text-align: center;">${matriculaDiscenteSerieMBean.ano}</td>
					<td>${linha.descricaoCompleta}</td>
					<td width="2%" align="right">
						<h:commandLink action="#{matriculaDiscenteSerieMBean.selecionarSerie}" >
							<h:graphicImage value="/img/seta.gif" style="overflow: visible;" title="Selecionar S�rie" />  
							<f:param name="id" value="#{linha.id}"/> 
						</h:commandLink>
					</td>
				</tr>
		   </c:forEach>
		</tbody>
	</table>
</c:if>	
	
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>