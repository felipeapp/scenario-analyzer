<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<style>
	table.listagem tr.destaque td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
	.dr-pnl-b{
		padding: 0;
	}
</style>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:form id="formCadastroEquipePrograma">
	<h2 class="title"><ufrn:subSistema /> &gt; Associar Docentes ao Programa de Pós-Graduação</h2>
	
	<a4j:keepAlive beanName="areaConcentracao" />
	
	<c:set var="confirmacao" value="if (!confirm('Deseja retirar esse docente do programa?')) return false" scope="request"/>
	
	<c:if test="${equipePrograma.exibeFormulario}">
		<table class="formulario" width="100%">
	
			<caption>Dados da Associação</caption>
	
			<tr>
				<th class="${not equipePrograma.podeAlterarPrograma?'rotulo':'required'}" width="20%" >Programa:</th>
				<td>
					<c:if test="${equipePrograma.podeAlterarPrograma}">
						<h:selectOneMenu value="#{ equipePrograma.obj.programa.id }" id="idPrograma"
							valueChangeListener="#{equipePrograma.selecionarPrograma}" onchange="submit()" >
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
							<f:selectItems value="#{ unidade.allProgramaPosCombo }" id="itensProgramasPosCombo"/>
						</h:selectOneMenu>
					</c:if>
	
					<c:if test="${not equipePrograma.podeAlterarPrograma}">
						${equipePrograma.obj.programa.nomeMunicipio}
					</c:if>
				</td>
			</tr>
			<c:if test="${equipePrograma.obj.id == 0}">
				<tr>
					<th class="required" style="vertical-align: middle; background-position: right; ">Docente:</th>
					<td>
							<h:inputHidden value="#{equipePrograma.obj.servidor.id}" id="idServidor"/>
							<c:set var="idAjax" value="formCadastroEquipePrograma:idServidor"/>
							<c:set var="nomeAjax" value="equipePrograma.obj.servidor.nome"/>
							<c:set var="nomeDocente" value="${equipePrograma.obj.servidor.pessoa.nome}"/>
							<%@include file="/WEB-INF/jsp/include/ajax/docente_jsf.jsp" %>
					</td>
				</tr>
			</c:if>
			<c:if test="${equipePrograma.obj.id > 0}">
				<tr>
					<th class="required" style="vertical-align: middle; background-position: right; ">Docente:</th>
				
					<td>
					<h:inputText value="#{equipePrograma.obj.nome}" disabled="true" size="70" id="nomePrograma"></h:inputText>
	
					</td>
				</tr>
			</c:if>
			<tr>
				<th>Área de Concentração Principal:</th>
				<td>
					<a4j:region>
					<h:selectOneMenu value="#{ equipePrograma.obj.areaConcentracaoPrincipal.id }" 
					id="selectAreaConcentracaoPrincipal" valueChangeListener="#{equipePrograma.carregarLinhas}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{ areaConcentracao.allFromProgramaCombo }"/>
						<a4j:support event="onchange" reRender="linhasPesquisa" />
					</h:selectOneMenu>
					<a4j:status>
			            <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
		            </a4j:status>
		            </a4j:region>
				</td>
			</tr>
			<tr>
				<th >Linhas de Pesquisa:</th>
				<td>
					<rich:panel id="linhasPesquisa" 
						style="max-height: 200px; overflow: auto; width: 95%; border: 1px solid #C8D5EC; min-height: 20px;">
					<table>
					<a4j:repeat var="linha" value="#{equipePrograma.possiveisLinhas}" id="loopLinhasPesquisas">
						<tr>
							<td><h:selectBooleanCheckbox id="checkLinhas" value="#{linha.selecionado}" styleClass="noborder" /></td>
							<td><h:outputText value="#{linha}"/></td>
						</tr>
					</a4j:repeat>
					</table>
					</rich:panel>
				</td>
			</tr>
			<tr>
				<th class="required" style="vertical-align: middle; background-position: right; ">Vínculo:</th>
				<td>
					<h:selectOneRadio value="#{ equipePrograma.obj.vinculo.id }" id="radioVinculo">
						<f:selectItems value="#{ vinculoEquipe.allCombo }" id="itensVinculosEquipe"/>
					</h:selectOneRadio>
				</td>
			</tr>
			<tr>
				<th class="required" style="vertical-align: middle; background-position: right; ">Nível:</th>
				<td>
					<h:selectOneRadio value="#{ equipePrograma.obj.nivel.id }" id="radioNivel">
						<f:selectItems value="#{ nivelEquipe.allCombo }" id="itensNiveisEquipe"/>
					</h:selectOneRadio>
				</td>
			</tr>
			<tr>
				<th >Mestrado:</th>
				<td>
					<h:selectBooleanCheckbox value="#{equipePrograma.obj.mestrado}"  id="checkMestrado"/>
				</td>
			</tr>
			<tr>
				<th >Doutorado:</th>
				<td>
					<h:selectBooleanCheckbox value="#{equipePrograma.obj.doutorado}"  id="checkDoutorado"/>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{equipePrograma.confirmButton}" action="#{equipePrograma.cadastrar}"  id="btnConfirm"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{equipePrograma.cancelar}"  id="btnCancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
			class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
		<br>
		</center>
	</c:if>
	<c:if test="${equipePrograma.obj.programa.id  > 0 and empty equipePrograma.equipeDoPrograma}">
		<br />
	
			<div class="infoAltRem">
				<img src="/sigaa/img/adicionar.gif">: <h:commandLink action="#{equipePrograma.preCadastrar}" id="cadastrarMembro" value="Cadastrar Membro" />
			</div>
			<br />
	
		<br /><br />
			<center>O programa ${equipePrograma.obj.programa.siglaNome} não possui equipe cadastrada </center>
		<br /><br />
	</c:if>
	</h:form>
	 <c:if test="${not empty equipePrograma.equipeDoPrograma}">
		<center>
		<h:form id="formIcones">
			<div class="infoAltRem">
				<img src="/sigaa/img/adicionar.gif">: <h:commandLink action="#{equipePrograma.preCadastrar}" id="cadastrarMembro" value="Cadastrar Membro" />
				<img src="/sigaa/img/comprovante.png">: Visualizar Detalhes
				<img src="/sigaa/img/alterar.gif">: Alterar Dados da Associação
				<img src="/sigaa/img/delete.gif">: Retirar Docente do Programa
			</div><br>
		</h:form>
		<h:form id="formListagem">
	 	<table class="listagem" >
	 		<caption>Equipe de docentes do programa ${equipePrograma.obj.programa.siglaNome} </caption>
	 		<c:set var="vinculoAtual" value="-1" />
	 		<c:forEach var="equipe" varStatus="status" items="#{equipePrograma.equipeDoPrograma}">
	 			<c:if test="${equipe.vinculo.id != vinculoAtual}">
		 			<c:set var="vinculoAtual" value="${equipe.vinculo.id}" />
					<c:set var="subTotal" value="0" />
			 		<tr class="destaque">
			 			<td colspan="1"></td>
			 			<td colspan="1" style="padding-left: 3px;">${equipe.vinculo.denominacao}</td>
			 			<%-- <td width="7%">Área de Concentração</td>--%>
			 			<td width="7%" style="padding-left: 3px; text-align: left;">Categoria</td>
			 			<td width="7%" style="padding-left: 3px; text-align: left;">Nível</td>
			 			<td width="3%" style="padding-left: 3px; text-align: center;">Mest.</td>
			 			<td width="3%" style="padding-left: 3px; text-align: center;">Dout.</td>
			 			<td colspan="2"></td>
			 		</tr>
	 			</c:if>
				<c:set var="subTotal" value="${subTotal + 1}" />
	 			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
	 				<td>
	 					<img src="${ctx}/img/comprovante.png" onclick="exibirDetalhes(${equipe.id});" style="cursor: pointer"/>
	 				</td>
	 				
	 				<td>${equipe}
	 					<c:if test="${equipe.servidorUFRN}"><span style="font-size: x-small;">(${equipe.cpf})</span></c:if> 
	 				</td>
	 				<%--<td width="7%" align="center" style="font-size: x-small;">${equipe.areaConcentracaoPrincipal}</td>--%>
	 				<td align="left" style="font-size: x-small;">
	 				<c:if test="${not equipe.servidorUFRN}">
		 				${equipe.categoria}
	 				</c:if>
	 				<c:if test="${equipe.servidorUFRN}">
						${equipe.servidor.classeFuncional.denominacao}	 				
	 				</c:if>
	 				</td>
	 				<td align="left" style="font-size: x-small; padd">${equipe.nivel}</td>
	 				<td align="center" style="font-size: x-small;">
	 					<ufrn:format type="bool_sn" valor="${equipe.mestrado}"/>
	 				</td>
	 				<td align="center" style="font-size: x-small;">
	 				<ufrn:format type="bool_sn" valor="${equipe.doutorado}"/>
	 				</td>
	 				<td >
						<h:commandLink action="#{equipePrograma.editar}" title="Alterar dados da associação" id="linkAlterarDadosAssociacao">
							<f:param name="id" value="#{equipe.id}" id="equipeID"/>
							<h:graphicImage value="/img/alterar.gif"/>
						</h:commandLink>
	 				</td>
	 				<td>
						<h:commandLink onclick="#{confirmacao}" action="#{equipePrograma.remover}" title="Retirar docente do programa">
							<f:param name="id" value="#{equipe.id}"/>
							<h:graphicImage value="/img/delete.gif"/>
						</h:commandLink>
	 				</td>
	 			</tr>
	 			
	 			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="display: none" id="trDetalhes${equipe.id}" >	
	 			
	 				<td></td>
	 				<td colspan="8">
	 					<c:set var="color" value="${status.index % 2 == 0 ? '#F9FBFD' : '#EDF1F8'}"/>
	 					<table width="100%">
	 						<tr style="background-color: ${color}" align="left" style="text-align: left">
	 							<th width="30%"><b>Área de concentração principal:</b></th>
	 							<td style="text-align: left">${equipe.areaConcentracaoPrincipal}</td>
	 						</tr>
 							<tr style="background-color: ${color}">
 								<th><b>Linhas de pesquisa:</b></th>
 								<td style="text-align: left">
	 								<c:forEach var="linha" items="${equipe.linhasPesquisa}" >
		 								${linha}<br/>
			 						</c:forEach>
 								</td>
 							</tr>
	 					</table>
	 				</td>
	 			
	 			</tr>
	 		</c:forEach>
	 	</table>
		</h:form>
		</center>

	 </c:if>
</f:view>
<script type="text/javascript">
<!--
	function exibirDetalhes(idEquipe){
		var linha = 'trDetalhes'+ idEquipe;
		 $(linha).toggle();
	}
-->
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>