<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<style>
	.colSemana{width:100px;text-align: left; }
	.colAgendamento{width:120px;text-align: center; }
	.colQtd{width:120px;text-align: center; }
	.colInscritos{width:120px;text-align: right; }
	.colIcone{width:10px;text-align: center;}
</style>
<f:view>
	<h2><ufrn:subSistema /> > Processo Seletivo > Acompanhamento dos Agendamentos</h2>
	<h:messages ></h:messages>

	<center>
		<h:form>
		<div class="infoAltRem" style="text-align: center; width: 100%">
			<h:graphicImage value="/img/listar.gif" style="overflow: visible;"/>
  			<h:commandLink value="Listar processos seletivos cadastrados" action="#{processoSeletivo.listar}"/>
  			<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>: Incrementar nº máximo de inscritos
		</div>
		</h:form>
	</center>
	
	<br clear="all"/>
	<a4j:region>
	<h:form id="form" enctype="multipart/form-data">
	
		<table class="formulario" width="80%">
			<caption><h:outputText value="#{editalProcessoSeletivo.obj.nome}" /> </caption>
			<thead>
				<tr>
					<td class="colSemana" >
					Dias da Semana
					</td>					
					<td class="colAgendamento" >
					Data de Agendamento
					</td>
					<td class="colQtd" >
					Nº de Inscritos Cadastrados
						</td>
					<td class="colInscritos" >
					Nº Máximo de Inscritos
					</td>
					<td class="colIcone"></td>
				</tr>
			</thead>
		</table>
		<center>
		<c:choose>
			<c:when test="${not empty editalProcessoSeletivo.obj}">
				<t:dataTable  id="dtTblAgenda"   value="#{editalProcessoSeletivo.obj.agendas}" 
					var="campoControle" rowIndexVar="linha" 
				 	columnClasses="colSemana,colAgendamento,colQtd,colInscritos,colIcone"  width="80%">
				 	<h:column>
						<h:outputText value="#{campoControle.diaSemana}"/>
					</h:column>
				 	<h:column>
						<h:outputText value="#{campoControle.dataAgenda}"/>
					</h:column>
					<h:column>
						<h:outputText value="#{campoControle.qtdIncritosCadastrados}"/>
					</h:column>
					<h:column>
						<h:inputText size="3" maxlength="3" readonly="true"  value="#{campoControle.qtd}"/>
					</h:column>
					<h:column >
						<a4j:commandLink reRender="dtTblAgenda" actionListener="#{editalProcessoSeletivo.incrementaQtdMaxInsc}">
							<h:graphicImage url="/img/adicionar.gif" />
							<f:param name="posicao" value="#{linha}" />
						</a4j:commandLink>
					</h:column>
				</t:dataTable>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan="4">
						Nenhuma inscrição até momento.
					</td>
				</tr>
			</c:otherwise>		
		</c:choose>	
		</center>
		<t:dataTable var="" columnClasses="" ></t:dataTable>
		<table class="formulario" width="80%">
			<tfoot>
				<tr>
					<td align="center" colspan="5">
						<h:commandButton value="Alterar Nº Máx. de Inscritos" onclick="if(confirm('Deseja realizar essa operação?')) return true; else return false;" 
						action="#{editalProcessoSeletivo.cadastrar	}" />
						<h:commandButton value="Cancelar" onclick="#{confirm}" 
						action="#{processoSeletivo.cancelar}" />
						<h:inputHidden value="#{editalProcessoSeletivo.obj.id}" />
					</td>
				</tr>
			</tfoot>
		</table>
		<a4j:poll id="polling" interval="650000"  enabled="true" action="#{editalProcessoSeletivo.carregaAgenda}"/>	
	</h:form>
	</a4j:region>
	
	<br />
	<center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/>
	   <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>