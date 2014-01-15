<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<style>
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
</style>
	<h2><ufrn:subSistema /> >  Carga Horária em Residência</h2>

<h:form id="formBusca">
	
	<h:outputText value="#{residenciaMedica.create}" />

		<table class="formulario" width="60%">
			<caption>Buscar Carga Horária em Residência</caption>
			<tbody>
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{residenciaMedica.checkBuscaAno}" id="selectBuscaAno" styleClass="noborder"/>
						</td>
				    	<td> <label for="selectBuscaAno" onclick="$('formBusca:selectBuscaAno').checked = !$('formBusca:selectBuscaAno').checked;"> Ano-Período: </label></td>
				    	<td>
				    		 <h:inputText value="#{residenciaMedica.ano}" size="4" maxlength="4" onkeyup="formatarInteiro(this)" onfocus="javascript:$('formBusca:selectBuscaAno').checked = true;"/> -
					    	 <h:inputText value="#{residenciaMedica.periodo}" size="1" maxlength="1" onkeyup="formatarInteiro(this)" onfocus="javascript:$('formBusca:selectBuscaAno').checked = true;"/>
				    	</td>
				    </tr>
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{residenciaMedica.checkBuscaPrograma}" id="selectPrograma"  styleClass="noborder"/>
						</td>
		    			<td> <label for="selectPrograma" onclick="$('formBusca:selectPrograma').checked = !$('formBusca:selectPrograma').checked;"> Programa: </label></td> 
						<td>
						<h:selectOneMenu id="programa" value="#{residenciaMedica.obj.programaResidenciaMedica.id}"
							onkeyup="formatarInteiro(this)" onfocus="javascript:$('formBusca:selectPrograma').checked = true;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{programaResidencia.allUnidadeCombo}" />
						</h:selectOneMenu>
			    	   </td>
		  	       </tr>	    
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{residenciaMedica.checkBuscaRelatorio}" id="selectRelatorio"  styleClass="noborder"/>
						</td>
						<td colspan="2"> 
							<label for="selectRelatorio" onclick="$('formBusca:selectRelatorio').checked = !$('formBusca:selectRelatorio').checked;">
								Exibir resultado da consulta em formato de relatório
							</label>
						</td>
		  	       </tr>	    
			</tbody>
			<tfoot>
					<tr>
						<td colspan="4">
							<h:commandButton value="Buscar" action="#{ residenciaMedica.relatorio }"
							id="Botaorelatorio" /> 
							<h:commandButton value="Cancelar" action="#{ residenciaMedica.cancelamento }" 
							id="BotaoCancelar" onclick="#{confirm}" />
						</td>
					</tr>
			</tfoot>
		</table>
	 <br />
	
	<c:set var="teste" value="0" />
	<c:set var="total" value="#{residenciaMedica.total}"/>
	
	<c:if test="${total != teste}">
	
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" />
	   <h:commandLink action="#{residenciaMedica.preCadastrar}" value="Cadastrar Nova CH em Residência" />
	   <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>:
		Visualizar CH em Residência	  
	   <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>:
		Alterar CH em Residência
	  <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>:
		Remover CH em Residência <br />
	 </div>
	
	<table class=listagem width="80%" border="1">
 	 <caption class="listagem">Cargas Horárias em Residências</caption>
			<thead>				
				<tr>
					<th><b>Programa</b></th>
					<th><b>Hospital</b></th>
					<th><b>Servidor</b></th>
					<th style="text-align: center;"><b>Ano/Período</b></th>
					<th style="text-align: right;"><b>CH Semanal</b></th>
					<th colspan="3"></th>
				</tr>
			</thead>
 	   <c:forEach items="#{residenciaMedica.lista}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${item.nome}</td>
					<td>${item.hospital}</td>
					<td>${item.nome_servidor}</td>
					<td align="center">${item.ano}.${item.semestre}</td>
					<td align="right">${item.ch_semanal}h</td>
					<td align="right">
						<h:commandLink title="Visualizar CH em Residência" style="border: 0;" action="#{residenciaMedica.visualizar}" >
							<f:param name="id" value="#{item.id}" />
							<h:graphicImage url="/img/view.gif" alt="Visualizar CH em Residência" />
						</h:commandLink>
					</td>
					<td align="right">
						<h:commandLink title="Alterar CH em Residência" style="border: 0;" action="#{residenciaMedica.atualizar}" >
							<f:param name="id" value="#{item.id}" />
							<h:graphicImage url="/img/alterar.gif" alt="Alterar CH em Residência" />
						</h:commandLink>
					</td>
					<td align="right">
						<h:commandLink title="Remover CH em Residência" style="border: 0;" action="#{residenciaMedica.remover}" onclick="#{confirmDelete}" >
							<f:param name="id" value="#{item.id}" />
							<h:graphicImage url="/img/delete.gif" alt="Remover CH em Residência" />
						</h:commandLink>
					</td>
			</tr>
		</c:forEach>
	</table>
	</c:if>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>