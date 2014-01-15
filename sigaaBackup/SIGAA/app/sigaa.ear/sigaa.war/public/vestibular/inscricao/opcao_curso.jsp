<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp"%>

<f:view>
	<h2 class="tituloPagina">Op��es de Curso</h2>
	
	<div class="descricaoOperacao">
		<b>Caro Candidato,</b><br/>
	Uma vez escolhido o curso, e conclu�da a inscri��o, as op��es
	informadas n�o poder�o ser alteradas. Contudo, voc� poder� realizar uma
	nova inscri��o para outros cursos, tantas vezes quantas quiser.<br/>
	A inscri��o que ser� validada ser� a �ltima paga, ou a mais recente
	quando for o caso de candidato isento.<br />
	</div>
	
	<h:form id="form">
		<table class="formulario" width="90%">
			<caption>Selecione sua(s) op��o(�es) de curso(s)</caption>
			<tbody>
				<tr>
					<td>
					<table width="100%" class="subFormulario">
						<caption>Primeira op��o</caption>
						<tr>
							<th class="required" width="15%">Munic�pio:</th>
							<td><a4j:region>
								<h:selectOneMenu id="municipio1"
									value="#{inscricaoVestibular.idMunicipioOpcao1}"
									disabled="#{cursoGrad.readOnly}">
									<a4j:support event="onchange"
										reRender="primeiraOpcao, municipio2, segundaOpcao"
										action="#{inscricaoVestibular.carregaListaPrimeiraOpcao}" />
									<f:selectItems
										value="#{inscricaoVestibular.municipiosPrimeiraOpcaoCombo}" />
								</h:selectOneMenu>
								<a4j:status>
									<f:facet name="start">
										<h:graphicImage value="/img/indicator.gif">Carregando a lista de cursos...</h:graphicImage>
									</f:facet>
								</a4j:status>
							</a4j:region></td>
						</tr>
						<tr>
							<th class="required">Curso:</th>
							<td><a4j:region>
								<h:selectOneMenu id="primeiraOpcao" immediate="true"
									value="#{inscricaoVestibular.idPrimeiraOpcaoCurso}">
									<f:selectItems
										value="#{inscricaoVestibular.primeiraOpcaoCombo}" />
									<a4j:support event="onchange"
										reRender="segundaOpcao, municipio2, lingua, avisoLingua"
										action="#{inscricaoVestibular.carregaMunicipiosSegundaOpcao }" />
								</h:selectOneMenu>
								<a4j:status>
									<f:facet name="start">
										<h:graphicImage value="/img/indicator.gif">Aguarde...</h:graphicImage>
									</f:facet>
								</a4j:status>
							</a4j:region></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td>
					<table width="100%" class="subFormulario">
						<caption>Segunda op��o</caption>
						<tr>
							<th width="15%">Munic�pio:</th>
							<td><a4j:region>
								<h:selectOneMenu id="municipio2"
									value="#{inscricaoVestibular.idMunicipioOpcao2}"
									disabled="#{cursoGrad.readOnly}">
									<a4j:support event="onchange" reRender="segundaOpcao"
										action="#{inscricaoVestibular.carregaListaSegundaOpcao }" />
									<f:selectItems
										value="#{inscricaoVestibular.municipiosSegundaOpcaoCombo}" />
								</h:selectOneMenu>
								<a4j:status>
									<f:facet name="start">
										<h:graphicImage value="/img/indicator.gif">Carregando a lista de cursos...</h:graphicImage>
									</f:facet>
								</a4j:status>
							</a4j:region></td>
						</tr>
						<tr>
							<th>Curso:</th>
							<td>
								<a4j:region>
									<h:selectOneMenu id="segundaOpcao" 
										value="#{inscricaoVestibular.idSegundaOpcaoCurso}">
										<f:selectItems value="#{inscricaoVestibular.segundaOpcaoCombo}" />
										<a4j:support event="onchange"
											reRender="lingua, avisoLingua"
											action="#{inscricaoVestibular.verificaLinguaEstrangeira }" />
									</h:selectOneMenu>
									<a4j:status>
										<f:facet name="start">
											<h:graphicImage value="/img/indicator.gif">Aguarde...</h:graphicImage>
										</f:facet>
									</a4j:status>
								</a4j:region>
							</td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td>
					<table width="100%" class="subFormulario">
						<caption>Outras Informa��es</caption>
						<tr>
							<th class="required" width="25%">L�ngua Estrangeira:</th>
							<td>
								<h:selectOneMenu id="lingua"
									value="#{inscricaoVestibular.obj.linguaEstrangeira.id}"
									disabled="#{inscricaoVestibular.linguaObrigatoria}">
									<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
									<f:selectItems value="#{linguaEstrangeira.allCombo}" />
								</h:selectOneMenu>
								<a4j:outputPanel id="avisoLingua">
									<h:outputText value="(Obrigat�ria)" style="color: red;" rendered="#{inscricaoVestibular.linguaObrigatoria}"/>
								</a4j:outputPanel>
							</td>
						</tr>
						<tr>
							<th class="required">Regi�o Preferencial de Prova:</th>
							<td>
								<h:selectOneMenu id="regiaoProva" immediate="true"
									value="#{inscricaoVestibular.obj.regiaoPreferencialProva.id}">
									<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
									<f:selectItems value="#{regiaoPreferencialProva.allCombo}" />
								</h:selectOneMenu>
								<ufrn:help>
									Na grande Natal, a COMPERVE tentar� atender � demanda dos candidatos, dentro do limite de
									locais dispon�veis para realiza��o das provas, n�o se obrigando a aplicar as provas no local escolhido pelo
									candidato.
								</ufrn:help>
							</td>
						</tr>
						<c:if test="${inscricaoVestibular.obj.processoSeletivo.opcaoBeneficioInclusao}">
							<tr>
								<th class="obrigatorio" width="25%" valign="top">Benef�cio de Inclus�o:</th>
								<td>
									<h:selectOneRadio id="argumentoInclusao" value="#{inscricaoVestibular.obj.optouBeneficioInclusao}"
										layout="pageDirection">
										<f:selectItems value="#{inscricaoVestibular.simNao}" />
									</h:selectOneRadio>
								</td>
							</tr>
						</c:if>
					</table>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="<< Passo Anterior" id="voltar" action="#{acompanhamentoVestibular.voltarPassoSelecaoCurso}" immediate="true" />
						<h:commandButton value="Cancelar" onclick="#{confirm}" id="cancelar" action="#{inscricaoVestibular.cancelar}" immediate="true" />
						<h:commandButton id="submerOpcoes" value="Pr�ximo Passo >>" action="#{inscricaoVestibular.submeterOpcoes}" /> 
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<center>
			<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> 
		</center>
		<br>
		<br>
	</h:form>
</f:view>

<%@include file="/public/include/rodape.jsp"%>