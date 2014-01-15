<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script type="text/javascript" src="/shared/loadScript?src=javascript/jquery.tablesorter.min.js"></script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> > Valida��o de Relat�rios de A��es Acad�micas</h2>
	<h:messages />
	<div class="descricaoOperacao">
		<b>Aten��o:</b> Selecione uma Unidade Proponente e clique em buscar para que sejam listados 
		todos os relat�rios pendentes de valida��o na unidade selecionada. 
	</div>


	<h:form id="form">

	<table class="formulario" width="90%">
			<caption>Busca por Autoriza��es</caption>
			<tbody>
				<tr>
			    	<th width="20%" class="required"> Unidade Proponente: </th>
			    	<td colspan="2">
						<h:selectOneMenu id="buscaUnidade" value="#{autorizacaoDepartamento.unidade.id}" style="width:90%" >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{unidade.unidadesProponentesProjetosCombo}"/>
						</h:selectOneMenu>
			    	 </td>
			    </tr>
			</tbody>
			<tfoot>			    
			    <tr>
			    	<td align="center" colspan="3">
			    		<h:commandButton value="Buscar" action="#{autorizacaoDepartamento.listarRelatoriosAcoes}" />
			    		<h:commandButton value="Cancelar" action="#{autorizacaoDepartamento.cancelar}" id="bt_cancelar" onclick="#{confirm}" /> 
			    	</td>
			    </tr>
			</tfoot>
	</table>
		
	<br/>

	<c:if test="${not empty autorizacaoDepartamento.relatoriosUnidade && autorizacaoDepartamento.unidade.id != 0}">
		<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif"style="overflow: visible;" styleClass="noborder"/>: Visualizar Projeto
		    <h:graphicImage value="/img/extensao/form_green.png"style="overflow: visible;" styleClass="noborder"/>: Visualizar Relat�rio	    
		    <h:graphicImage value="/img/seta.gif"style="overflow: visible;" styleClass="noborder"/>: Analisar Relat�rio
		</div>
	
			<table class="listagem tablesorter" id="listagem">
					<caption class="listagem"> Relatorios de A��es de Acad�micas do Departamento Selecionado</caption>
						<thead>
							<tr>
								<th>C�digo</th>
								<th>Ano</th>
								<th width="40%">T�tulo da A��o</th>
								<th>Tipo</th>
								<th>Analisado em</th>
								<th></th>
								<th></th>
								<th></th>						
							</tr>
						</thead>
							
							<c:forEach items="#{autorizacaoDepartamento.relatoriosUnidade}" var="item" varStatus="status">
				               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
										<td>${item.atividade.codigo}</td>
										<td>${item.atividade.ano}</td>
										<td>${item.atividade.titulo}</td>
										<td>${item.tipoRelatorio.descricao}</td>
										<td><fmt:formatDate value="${item.dataValidacaoDepartamento}" pattern="dd/MM/yyyy HH:mm:ss" /> ${item.dataValidacaoDepartamento == null ? '<font color=red>N�O ANALISADO</font>': ''}</td>
										
										<td  width="3%">											
												<h:commandLink title="Visualizar Projeto" 
												action="#{atividadeExtensao.view}" style="border: 0;">											
											       <f:param name="id" value="#{item.atividade.id}"/>
									               <h:graphicImage url="/img/view.gif" />
												</h:commandLink>
										</td>
										
										<td  width="3%">											
												<h:commandLink title="Visualizar Relat�rio" 
												action="#{relatorioAcaoExtensao.view}" style="border: 0;">											
											       <f:param name="id" value="#{item.id}"/>
									               <h:graphicImage url="/img/extensao/form_green.png" />
												</h:commandLink>
										</td>
	
										<td  width="3%">											
												<h:commandLink title="Analisar Relat�rio" 
												action="#{autorizacaoDepartamento.analisarRelatorio}" style="border: 0;"
												rendered="#{item.departamentoPodeAnalisar}">
											       <f:param name="idRelatorio" value="#{item.id}"/>
									               <h:graphicImage url="/img/seta.gif" />
												</h:commandLink>
										</td>
								</tr>
				 		   </c:forEach>
				 		   
	
			</table>
		</c:if>		
 		<c:if test="${empty autorizacaoDepartamento.relatoriosUnidade && autorizacaoDepartamento.unidade.id != 0}">
	 	   <center><i>N�o h� relat�rios de projetos submetidos para an�lise desta unidade</i></center>
 		</c:if>
		<rich:jQuery selector="#listagem" query="tablesorter( {headers: {5: { sorter: false },6: { sorter: false },7: { sorter: false } } });" timing="onload" />
</h:form>
<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> </center><br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>