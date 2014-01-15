<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
 
<script>
// XXX000-00
function formatarCodigo(campo, event){
	var tecla = (event != null) ? event.keyCode : 0;
	var regExp;
	var txt = campo.value;
	// verificar letras
	if (campo.value.length <= 2){
		if ((tecla >= 65) && (tecla <= 90)){
			campo.value = txt.toUpperCase();
		} else {
			regExp = /\d|\s|[,.;<>:+=_]/gi;
			campo.value = txt.replace(regExp, "");
		}
	} else if (campo.value.length == 3){
		if ((tecla >= 65) && (tecla <= 74)){
			campo.value = txt.toUpperCase();
		} else {
			regExp = /[k-z]|\d|\s|[,.;<>:+=_]/gi;
			campo.value = txt.substring(0,2) + txt.substring(2,3).replace(regExp, "");
		}
	} else {
		if (!((tecla >= 48) && (tecla <= 57)) && txt.length <= 6){
			regExp = /\D/gi;
			campo.value = txt.substring(0,3) + txt.substr(3).replace(regExp, "");
		}
		else if (!((tecla >= 48) && (tecla <= 57)) && txt.length >= 7){
			regExp = /\D/gi;
			campo.value = txt.substring(0,7) + txt.substr(7).replace(regExp, "");
		}
		if (campo.value.length >= 6 && !(txt.charAt(6) == "-") && tecla != 8){
			campo.value = txt.substring(0,6) + "-" + txt.substr(6);
		}
	} 
}
</script>

<f:view>
	<h2><ufrn:subSistema /> &gt; Grupos de Pesquisa</h2>
	<h:outputText value="#{grupoPesquisa.create}" />
	<h:form id="formConsulta">

		<table class="formulario" align="center" width="60%">
		<caption class="listagem">Critérios de Busca das Bases</caption>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{grupoPesquisa.filtroNome}" id="checkNome" styleClass="noborder"/> </td>
				<td width="22%"><label for="checkNome" onclick="$('formConsulta:checkNome').checked = !$('formConsulta:checkNome').checked;">Nome:</label></td>
				<td>
					<h:inputText id="nome" value="#{grupoPesquisa.obj.nome}" size="60" onfocus="$('formConsulta:checkNome').checked = true;"/>
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{grupoPesquisa.filtroCoordenador}" id="checkCoordenador" styleClass="noborder"/> </td>
				<td width="22%"><label for="checkCoordenador" onclick="$('formConsulta:checkCoordenador').checked = !$('formConsulta:checkCoordenador').checked;">Coordenador:</label></td>
				<td>
					<h:inputText id="nomeCoordenador" value="#{grupoPesquisa.obj.coordenador.pessoa.nome}" size="60" onfocus="$('formConsulta:checkCoordenador').checked = true;"/>
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{grupoPesquisa.filtroArea}" id="checkArea" styleClass="noborder"/> </td>
				<td width="22%"><label for="checkArea" onclick="$('formConsulta:checkArea').checked = !$('formConsulta:checkArea').checked;">Grande Área:</label></td>
				<td>
					<h:selectOneMenu id="areaCNPQ"
						value="#{grupoPesquisa.obj.areaConhecimentoCnpq.id}" style="width: 70%;"
						onfocus="$('formConsulta:checkArea').checked = true;">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE UMA ÁREA DO CNPq --"/>
						<f:selectItems value="#{area.allAreasCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{grupoPesquisa.filtroStatus}" id="checkStatus" styleClass="noborder"/> </td>
				<td width="22%"><label for="checkStatus" onclick="$('formConsulta:checkStatus').checked = !$('formConsulta:checkStatus').checked;">Status:</label></td>
				<td>
					<h:selectOneMenu id="status"
						value="#{grupoPesquisa.obj.status}" style="width: 70%;"
						onfocus="$('formConsulta:checkStatus').checked = true;">
						<f:selectItem itemValue="-1" itemLabel="-- SELECIONE UM STATUS --"/>
						<f:selectItems value="#{grupoPesquisa.tiposStatusCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{grupoPesquisa.filtroCentro}" id="checkCentro" styleClass="noborder"/> </td>
				<td width="22%"><label for="checkCentro" onclick="$('formConsulta:checkCentro').checked = !$('formConsulta:checkCentro').checked;">Centro:</label></td>
				<td>
					<h:selectOneMenu id="centro"
						value="#{grupoPesquisa.centro}" style="width: 70%;"
						onfocus="$('formConsulta:checkCentro').checked = true;">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM CENTRO --"/>
						<f:selectItem itemValue="A" itemLabel="CB"/>
						<f:selectItem itemValue="B" itemLabel="CCET"/>
						<f:selectItem itemValue="C" itemLabel="CCHLA"/>
						<f:selectItem itemValue="D" itemLabel="CCS"/>
						<f:selectItem itemValue="E" itemLabel="CCSA"/>
						<f:selectItem itemValue="F" itemLabel="CERES"/>
						<f:selectItem itemValue="G" itemLabel="CT"/>
						<f:selectItem itemValue="H" itemLabel="EM"/>
						<f:selectItem itemValue="I" itemLabel="EAJ"/>
						<f:selectItem itemValue="J" itemLabel="ECT"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{grupoPesquisa.filtroAtivo}" id="checkAtivo" styleClass="noborder"/> </td>
				<td width="22%"><label for="checkSituacao" onclick="$('formConsulta:checkAtivo').checked = !$('formConsulta:checkAtivo').checked;">Situação:</label></td>
				<td>
					<h:selectOneRadio id="ativo"
						value="#{grupoPesquisa.obj.ativo}" style="width: 70%;"
						onfocus="$('formConsulta:checkAtivo').checked = true;">
						<f:selectItem itemValue="#{true}" itemLabel="Ativo" />
						<f:selectItem itemValue="#{false}" itemLabel="Inativo" />
					</h:selectOneRadio>
				</td>
			</tr>
			
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{grupoPesquisa.filtroCodigo}" id="checkCodigo" styleClass="noborder"/> </td>
				<td width="22%"><label for="checkCodigo" onclick="$('formConsulta:checkCodigo').checked = !$('formConsulta:checkCodigo').checked;">Código:</label></td>
				<td>
					<h:inputText id="codigo" value="#{grupoPesquisa.obj.codigo}" maxlength="9" size="10" onfocus="$('formConsulta:checkCodigo').checked = true;" 
						onkeyup="formatarCodigo(this,event)" /> <span style="color: gray;">Ex:XXX000-00</span>
				</td>
			</tr>
			<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton id="buscar" action="#{grupoPesquisa.buscar}" value="Buscar"/>
					<h:commandButton id="cancelar" action="#{grupoPesquisa.cancelar}" value="Cancelar" onclick="#{confirm}"/>
				</td>
			</tr>
			</tfoot>
		</table>
		<br/>
		<c:set var="lista" value="${grupoPesquisa.resultadosBusca}"/>
		
		
		<c:if test="${not empty lista}">
			<center>
				<div class="infoAltRem">
					<h:graphicImage value="/img/view.gif"    style="overflow: visible;"/>: Visualizar
					<h:graphicImage value="/img/avaliar.gif" style="overflow: visible;"/>: Alterar Status
					<h:graphicImage value="/img/certificate.png" style="overflow: visible;" width="19px;"/>: Certificado
					<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar
		        	<h:graphicImage value="/img/delete.gif"  style="overflow: visible;"/>: Remover
				</div>
			</center>	
			<table class="listagem">
				<caption class="listagem">Grupos de Pesquisa Encontrados ( ${fn:length(lista)} )</caption>
				<thead>
				<tr>
					<td>Código</td>
					<td>Nome</td>
					<td>Líder</td>
					<td>Área de conhecimento</td>
					<td>Status</td>
					<td colspan="5"></td>
				</tr>
				</thead>
				<c:forEach items="#{grupoPesquisa.resultadosBusca}" var="item" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${item.codigo}</td>
						<td>${item.nome}</td>
						<td>${item.coordenador.pessoa.nome}</td>
						<td>${item.areaConhecimentoCnpq.nome}</td>
						<td>${item.statusString}</td>
						<td width="3%">
							<h:commandLink title="Visualizar" action="#{propostaGrupoPesquisaMBean.visualizar}">
								<f:param name="id" value="#{item.id}"/>
								<h:graphicImage url="/img/view.gif"/>
							</h:commandLink>
						</td>
						<td width="3%">
							<h:commandLink title="Alterar Status" action="#{propostaGrupoPesquisaMBean.enviarParecer}">
								<f:param name="id" value="#{item.id}"/>
								<h:graphicImage url="/img/avaliar.gif"/>
							</h:commandLink>
						</td>
						<td width="3%">
							<h:commandLink title="Certificado" action="#{propostaGrupoPesquisaMBean.certificado}">
								<f:param name="id" value="#{item.id}"/>
								<h:graphicImage url="/img/certificate.png" width="19px;"/>
							</h:commandLink>
						</td>
						<td width="3%">
							<h:commandLink title="Alterar" action="#{propostaGrupoPesquisaMBean.continuar}">
								<f:param name="id" value="#{item.id}"/>
								<h:graphicImage url="/img/alterar.gif"/>
							</h:commandLink>
						</td>
						<td width="3%">
							<h:commandLink title="Remover" action="#{propostaGrupoPesquisaMBean.inativar}" onclick="#{confirmDelete}">
								<f:param name="id" value="#{item.id}"/>
								<h:graphicImage url="/img/delete.gif"/>
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="10" align="center"> <b>${fn:length(lista)} grupo(s) encontrado(s) </b></td>
					</tr>
				</tfoot>
			</table>
		</c:if>	
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>