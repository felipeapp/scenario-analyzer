<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" type="text/css" href="/sigaa/css/ensino/detalhes_discente.css"/>
<script type="text/javascript" src="/sigaa/javascript/graduacao/busca_discente.js"> </script>

<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
	
	table.listagem td.detalhesDiscente { display: none; padding: 0;}

.alinharDireita{ 
	text-align:right !important;
}
.alinharEsquerda{ 
	text-align:left !important;
} 
.alinharCentro{ 
	text-align:center !important;
}
.destacado{
	color: red;
}
</style>

<f:view>

	<h2 class="tituloPagina"> <ufrn:subSistema/> > Buscar bolsas</h2>
	
	<div class="descricaoOperacao">
		<p>	Caro usuário, </p>
		<p> realize consultas das bolsas de auxílio conforme os filtros disponibilizados abaixo.</p>
		<p> A operação <b>Definir Dias de Alimentação</b> somente é habilitada para as bolsas com situação de deferimento <b>Contemplado</b>.</p> <br />
	</div>
	
	<h:form id="formulario">
	<table class="formulario" width=70%>
		<caption> Informe os critérios de busca</caption>
		<tbody>
			<tr>
				<td width="30%" align="right" colspan="2"> Tipo da bolsa: <html:img page="/img/required.gif" style="vertical-align: center;" /></td>
				<td colspan="2"> 
						<h:selectOneMenu value="#{consultaBolsaAuxilioMBean.idTipoBolsaAuxilio}" style="width:400px;">
							<f:selectItem itemLabel="-- Selecione --" itemValue="0"/>
							<f:selectItems value="#{consultaBolsaAuxilioMBean.allTiposBolsas}" />
						</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<td align="right" width="1%">
					<h:selectBooleanCheckbox value="#{consultaBolsaAuxilioMBean.checkAnoPeriodo}" styleClass="noborder" id="checkAnoPeriodo"/>
				</td>

				<td align="right" width="18%"> Ano / Semestre: </td>
				
				<td> 
					<h:inputText id="ano" value="#{consultaBolsaAuxilioMBean.ano}" maxlength="4" size="4" onkeyup="return formatarInteiro(this);" onfocus = "getEl('formulario:checkAnoPeriodo').dom.checked = true;"/>.
					<h:inputText id="periodo" value="#{consultaBolsaAuxilioMBean.periodo}" maxlength="1" size="1" onkeyup="return formatarInteiro(this);" onfocus = "getEl('formulario:checkAnoPeriodo').dom.checked = true;"/> 
				 </td>
			</tr>
			
			<tr>
				<td align="right" width="1%">
					<h:selectBooleanCheckbox value="#{consultaBolsaAuxilioMBean.checkDeferimento}" styleClass="noborder" id="checkDeferimento"/>
				</td>
	
				<td align="left"> Deferimento: </td>
				
				<td colspan="3"> 
						<h:selectOneRadio value="#{consultaBolsaAuxilioMBean.deferimento}"
						onfocus = "getEl('formulario:checkDeferimento').dom.checked = true;" layout="pageDirection">
							<f:selectItems value="#{situacaoBolsaAuxilioMBean.allCombo}" />
						</h:selectOneRadio>
				</td>
			</tr>
			
			<tr>
				<td align="right">
					<h:selectBooleanCheckbox value="#{consultaBolsaAuxilioMBean.checkResidencia}" styleClass="noborder" id="checkResidencia"/>
				</td>
				
				<td align="left">Residência:</td>
				<td>
						<h:selectOneMenu id="residencia" value="#{consultaBolsaAuxilioMBean.idResidencia}" style="width:400px;"
							onfocus = "getEl('formulario:checkResidencia').dom.checked = true;">
							<f:selectItem itemLabel="-- Selecione --" itemValue="0"/>
							<f:selectItems value="#{consultaBolsaAuxilioMBean.allResidencias}" />
						</h:selectOneMenu>
				</td>
			</tr>		

			<tr>
				<td align="right">
					<h:selectBooleanCheckbox value="#{consultaBolsaAuxilioMBean.checkNivel}" styleClass="noborder" id="checkNivel"/>
				</td>
				
				<td align="left">Nível:</td>
				<td>
						<h:selectOneMenu value="#{consultaBolsaAuxilioMBean.nivel}"
							onfocus = "getEl('formulario:checkNivel').dom.checked = true;">
							<f:selectItem itemLabel="-- Selecione --" itemValue="0"/>
							<f:selectItem itemLabel="Graduação" itemValue="G"/>
							<f:selectItem itemLabel="Pós-graduação" itemValue="S" />
							<a4j:support event="onchange" action="#{consultaBolsaAuxilioMBean.definirNivelEnsino}" ajaxSingle="true" reRender="cursos"/>  
						</h:selectOneMenu>
				</td>
			</tr>
		
			<tr>
				<td align="right">
					<h:selectBooleanCheckbox value="#{consultaBolsaAuxilioMBean.checkMunicipio}" styleClass="noborder" id="checkMunicipio"/>
				</td>
				
				<td align="left">Município:</td>
				<td>
					<h:selectOneMenu id="campus" value="#{consultaBolsaAuxilioMBean.idMunicipio}" style="width:400px;"
						onfocus = "getEl('formulario:checkMunicipio').dom.checked = true;">
						<f:selectItem itemLabel="-- Selecione --" itemValue="0"/>
						<f:selectItems value="#{consultaBolsaAuxilioMBean.allMunicipiosCampusCombo}"/>
						<a4j:support event="onchange" action="#{consultaBolsaAuxilioMBean.definirNivelEnsino}" ajaxSingle="true" reRender="cursos"/>  
					</h:selectOneMenu>
				</td>
			</tr>	
						
			<tr>
				<td align="right">
					<h:selectBooleanCheckbox value="#{consultaBolsaAuxilioMBean.checkCurso}" styleClass="noborder" id="checkCurso"/>
				</td>
				<td align="left" width="15%">Curso:</td>
				<td>
					<h:selectOneMenu id="cursos" value="#{consultaBolsaAuxilioMBean.idCurso}" style="width:400px;"
						onfocus = "getEl('formulario:checkCurso').dom.checked = true;">
						<f:selectItem itemLabel="-- Selecione --" itemValue="0"/>
						<f:selectItems value="#{consultaBolsaAuxilioMBean.allCursos}" />
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<td align="right">
					<h:selectBooleanCheckbox value="#{consultaBolsaAuxilioMBean.checkStatus}" styleClass="noborder" id="checkStatus"/>
				</td>
				<td align="left" width="15%">Status Discente:</td>
				<td>
					<h:selectOneMenu id="status" value="#{consultaBolsaAuxilioMBean.idStatusDiscente}" style="width:400px;"
						onfocus = "getEl('formulario:checkStatus').dom.checked = true;">
						<f:selectItem itemLabel="-- Selecione --" itemValue="0"/>
						<f:selectItems value="#{consultaBolsaAuxilioMBean.allStatus}" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<td align="right">
					<h:selectBooleanCheckbox value="#{consultaBolsaAuxilioMBean.checkSexo}" styleClass="noborder" id="checkSexo"/>
				</td>
				<td>Sexo:</td>
				<td>
					<h:selectOneRadio value="#{consultaBolsaAuxilioMBean.sexo}" id="sexo" onfocus = "getEl('formulario:checkSexo').dom.checked = true;">
						<f:selectItem itemValue="M" itemLabel="Masculino" />
						<f:selectItem itemValue="F" itemLabel="Feminino" />
					</h:selectOneRadio>
				</td>
			</tr>								

		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton action="#{definirDiasAlimentacaoMBean.buscar}" value="Buscar" id="buscar"/>
					 <h:commandButton id="cancelar" value="Cancelar" action="#{definirDiasAlimentacaoMBean.cancelar}" 
					 	onclick="return confirm('Deseja cancelar a operação? Todos os dados digitados não salvos serão perdidos!');">
					 </h:commandButton>
				</td>
			</tr>
		</tfoot>
	</table>

	</h:form>
	<br/>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br/>
	
	<c:if test="${not empty definirDiasAlimentacaoMBean.listaBolsaAuxilio}">
		<br />
		<center>
			<div class="infoAltRem">
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR, SigaaPapeis.SAE_DEFINIR_DIAS_ALIMENTACAO } %>">
					<h:graphicImage value="/img/add_cal.png" style="overflow: visible;" />: Definir dias de alimentação
				</ufrn:checkRole>
			</div>
		</center>
		
		<h:form>
		<table class="listagem">
			<caption> Selecione abaixo o discente </caption>
			<thead>
			<tr>
				<th> Aluno </th>
				<th> Município </th>
				<th> Tipo da Bolsa </th>
				<th width="1%"> </th>
			</tr>
			<tbody>
				<c:forEach items="#{definirDiasAlimentacaoMBean.listaBolsaAuxilio}" var="bolsa" varStatus="loop">
				
						<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td>
								${bolsa.discente.matriculaNome}
							</td>
							
							<td>
								${bolsa.discente.curso.municipio.nome}
							</td>
							
							<td>
								${bolsa.tipoBolsaAuxilio.denominacao}
							</td>
												
							
							<c:if test="${ bolsa.permiteDefinirDiasAlimentacao }">
								<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR, SigaaPapeis.SAE_DEFINIR_DIAS_ALIMENTACAO }  %>">
								<td>
									<h:commandButton image="/img/add_cal.png"
										actionListener="#{definirDiasAlimentacaoMBean.iniciarCadastroDiasAlimentacao}" title="Definir dias de alimentação">
										<f:attribute name="idDiscente" value="#{bolsa.discente.id}"/>
										<f:attribute name="idBolsa" value="#{bolsa.id}"/>
									</h:commandButton>
								</td>
								</ufrn:checkRole>
							</c:if>
							
						</tr>
				</c:forEach>
			</tbody>
				<tfoot>
					<tr>
						<td colspan="4" style="text-align: center; font-weight: bold;">
							${fn:length(consultaBolsaAuxilioMBean.listaBolsaAuxilio)} bolsa(s) encontrada(s)
						</td>
					</tr>
				</tfoot>
		</table>
		</h:form>
	</c:if>


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>