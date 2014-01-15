<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.tituloRelatorio td {padding: 19px 0 0; border-bottom: 1px solid #555 ; font-weight: bold; font-size: 13px; }
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	table.formulario tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888; font-size: 0.9em;}
	
	table.formulario tr th { font-weight: bold; }
	table.formulario tr table th { font-weight: normal; font-style: italic; }
</style>
<f:view>
	<c:if test="${curriculo.graduacao}">
		<c:set var="_curso" value="#{curriculo.obj.matriz.curso}" />
	</c:if>
	<c:if test="${not curriculo.graduacao}">
		<c:set var="_curso" value="#{curriculo.obj.curso}" />
	</c:if>
	
		<h3 class="tituloTabela" style="width: 100%">Dados da Estrutura Curricular</h3>
		<table class="formulario" width="100%">
			<tr>
				<th width="35%">C�digo:</th>
				<td><h:outputText value="#{curriculo.obj.codigo }" /></td>
			</tr>
			<c:if test="${curriculo.graduacao}">
				<tr>
					<th>Matriz Curricular:</th>
					<td><h:outputText value="#{curriculo.obj.matriz.descricao }" /></td>
				</tr>
			</c:if>
			<c:if test="${not curriculo.graduacao}">
				<tr>
					<th>Curso:</th>
					<td><h:outputText value="#{curriculo.obj.curso.descricao}" /></td>
				</tr>
			</c:if>
			<tr>
				<th> Unidade de Vincula��o: </th>
				<td> ${_curso.unidade.codigoNome} </td>
			</tr>
			<tr>
				<th> Munic�pio de funcionamento: </th>
				<td> ${_curso.municipio.nomeUF} </td>
			</tr>
			<tr>
				<th>Per�odo Letivo de Entrada em Vigor:</th>
				<td><h:outputText value="#{curriculo.obj.anoEntradaVigor} . #{curriculo.obj.periodoEntradaVigor }" /></td>
			</tr>
			<tr>
				<th>Carga Hor�ria:</th>
				<td>
				<table>
					<tr>
						<th>Total M�nima</th>
						<td><h:outputText value="#{curriculo.obj.chTotalMinima}" /></td>
						<th>Optativas M�nima</th>
						<td><h:outputText value="#{curriculo.obj.chOptativasMinima}" /></td>
					</tr>
				</table>
				</td>
			</tr>

			<tr>
				<th>Cr�ditos Obrigat�rios:</th>
				<td>
					${curriculo.obj.crPraticos + curriculo.obj.crTeoricos}cr Total - ( ${curriculo.obj.crPraticos}cr Pr�ticos ) / ( ${curriculo.obj.crTeoricos}cr Te�ricos )
				</td>
			</tr>
			<tr>
				<th>Carga Hor�ria Obrigat�ria:</th>
				<td>
					${curriculo.obj.chPraticos + curriculo.obj.chTeoricos}h Total - ( ${curriculo.obj.chPraticos}h Pr�ticas ) / ( ${curriculo.obj.chTeoricos}h Te�ricas )
				</td>
			</tr>
			<tr>
				<th>Carga Hor�ria Obrigat�ria de Atividade Acad�mica Espec�fica:</th>
				<td>
					${curriculo.obj.chAAE} hrs
				</td>
			</tr>
			<tr>
				<th>Carga Hor�ria de Componentes Eletivos: </th>
				<td>
				<table>
					<tr>
						<th>M�xima</th>
						<td>(<h:outputText value="#{curriculo.obj.maxEletivos}" /> horas)</td>
						<th></th>
						<td></td>
						<th></th>
						<td></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<th>Carga Hor�ria por Per�odo Letivo:</th>
				<td>
				<table>
					<tr>
						<th>M�nima</th>
						<td>(<h:outputText value="#{curriculo.obj.chMinimaSemestre}" /> horas)</td>
						<th></th>
						<td></td>
						<th></th>
						<td></td>
					</tr>
				</table>
				</td>
			</tr>
			<c:if test="${curriculo.graduacao}">
				<tr>
					<th>Cr�ditos por Per�odo Letivo:</th>
					<td>
					<table>
						<tr>
							<th>M�nimo</th>
							<td><h:outputText value="#{curriculo.obj.crMinimoSemestre}" /></td>
							<th>M�dio</th>
							<td><h:outputText value="#{curriculo.obj.crIdealSemestre}" /></td>
							<th>M�ximo</th>
							<td><h:outputText value="#{curriculo.obj.crMaximoSemestre}" /></td>
						</tr>
					</table>
					</td>
				</tr>
			</c:if>
			<tr>
				<th>Prazos em Per�odos Letivos:</th>
				<td>
				<table>
					<tr>
						<th>M�nimo</th>
						<td><h:outputText value="#{curriculo.obj.semestreConclusaoMinimo}" /></td>
						<th>M�dio</th>
						<td><h:outputText value="#{curriculo.obj.semestreConclusaoIdeal}" /></td>
						<th>M�ximo</th>
						<td><h:outputText value="#{curriculo.obj.semestreConclusaoMaximo}" /></td>
					</tr>
				</table>
				</td>
			</tr>
			<c:if test="${curriculo.graduacao}">
				<tr>
					<td colspan="2">
						<table>
						<c:forEach items="${curriculo.semestres }" var="semestre">
							<tr class="tituloRelatorio">
								<td colspan="5">${semestre}� N�vel</td>
							</tr>
							<tr class="header">
								<td colspan="2" width="65%">Componente Curricular</td>
								<td width="15%">CH Detalhada</td>
								<td width="13%">Tipo</td>
								<td width="12%">Natureza</td>
							</tr>
							<c:set value="0" var="ch" />
							<c:forEach items="${curriculo.componentesAdicionados[semestre]}" var="cc" varStatus="linha">
								<c:set value="${ch + cc.componente.chTotal }" var="ch" />
								<tr class="componentes">
									<td>${cc.componente.codigo}</td>
									<td>
										${cc.componente.nome} - ${cc.componente.chTotal}h (${cc.componente.crTotal}cr)
									</td>
									<td style="font-size: 0.9em;">
										${cc.componente.detalhes.chAula}h (${cc.componente.detalhes.crAula}cr) aula 
										<br/> ${cc.componente.detalhes.chLaboratorio}h (${cc.componente.detalhes.crLaboratorio}cr) lab. 
									</td>
									<td><small>
									<c:if test="${not cc.componente.atividade}">${cc.componente.tipoComponente}</c:if>
									<c:if test="${cc.componente.atividade}">${cc.componente.tipoAtividade}</c:if>
									</small></td>
									<td width="8%"><small>${(cc.obrigatoria)?'OBRIGAT�RIA':'OPTATIVA'}</small></td>
								</tr>
							</c:forEach>
							<tr>
								<td colspan="5"><b>CH Total:</b> ${ch}hrs. &nbsp;&nbsp;&nbsp; </td>
							</tr>
						</c:forEach>
						</table>
					</td>
				</tr>
				<c:if test="${ not empty curriculo.obj.enfases }">
				<tr>
					<td colspan="2">&nbsp;<br/><br/>
						<table width="100%">
						<tr class="tituloRelatorio"><td>Grupos de Componentes Optativos</td></tr>
						<c:forEach var="grupo" items="${ curriculo.obj.enfases }">
						<tr class="header"><td>${ grupo.descricao }</td></tr>
						<c:forEach var="cc" items="${ grupo.componentes }">
						<tr class="componentes"><td>${ cc.componente.descricao }</td></tr>
						</c:forEach>
						<tr>
							<td><b>CH Total:</b> ${grupo.chTotal}hrs. &nbsp;&nbsp;&nbsp; </td>
						</tr>
						<tr>
							<td><b>CH M�nima:</b> ${grupo.chMinima}hrs. &nbsp;&nbsp;&nbsp;<br/>&nbsp; </td>
						</tr>
						</c:forEach>
						</table>
					</td>
				</tr>
				</c:if>
			</c:if>
			<c:if test="${not curriculo.graduacao}">
				<tr><td colspan="2" align="center">
					<table class="listagem" width="100%" >
						<caption>Componentes desse Curr�culo</caption>
						<c:set value="0" var="ch" />
						<c:forEach items="${curriculo.obj.curriculoComponentes}" var="cc">
							<c:set value="${ch + cc.componente.chTotal }" var="ch" />
							<tr>
								<td>${cc.componente.descricao}</td>
								<td width="8%"><i>${(cc.obrigatoria)?'Obrigat�ria':'Optativa'}</i></td>
							</tr>
						</c:forEach>
						<tr style="background-color: #EFEBDE">
							<td colspan="3"><b>CH Total:</b> ${ch}hrs. &nbsp;&nbsp;&nbsp; <b>Total de Cr�ditos:</b> <fmt:formatNumber pattern="##" value="${ch/15 }" /></td>
						</tr>
					</table>
				</td></tr>
			</c:if>
		</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
