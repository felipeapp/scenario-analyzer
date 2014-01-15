<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="title"><ufrn:subSistema /> > Parâmetros do Sistema</h2>
	<div class="descricaoOperacao">
		<p><b>Caro usuário,</b></p>
		<p>Informe, a seguir, os parâmetros utilizados na unidade acadêmica abaixo detalhada.</p>
	</div>
	<table class="visualizacao">
		<tr>
			<th width="20%">Unidade Responsável:</th>
			<td>${parametros.obj.unidade.nome }</td>
		</tr>
		<tr>
			<th>Nível de Ensino:</th>
			<td>${parametros.obj.nivelDescr}</td>
		</tr>
	</table>
	<br/>
	<h:form id="form">
		<table class="formulario" width="100%">
			<caption>Defina os Valores dos Parâmetros</caption>
			<tbody>
				<tr><td colspan="2" class="subFormulario">Matrícula, Trancamentos e Reprovação em Componentes Curriculares</td></tr>
				<tr>
					<th class="obrigatorio">Percentual Máximo Cumprido para Permitir Trancamento:</th>
					<td><h:inputText value="#{parametros.obj.percentualMaximoCumpridoTrancamento}" size="6" 
							onkeyup="return formatarInteiro(this);" id="percentualMaximoCumpridoTrancamento" maxlength="3">						
						</h:inputText>
						<ufrn:help>O valor informado, entre 0 e 100, é o percentual do período da turma cumprida pelo discente para poder permitir trancamento.</ufrn:help>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Número Máximo de Trancamentos de PROGRAMA:</th>
					<td><h:inputText value="#{parametros.obj.maxTrancamentos}" size="3" 
						onkeyup="return formatarInteiro(this);" id="maxTrancamentos" maxlength="2" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">Número Máximo de Trancamentos de MATRÍCULA:</th>
					<td><h:inputText value="#{parametros.obj.maxTrancamentosMatricula}" size="3" 
						onkeyup="return formatarInteiro(this);" id="maxTrancamentosMatricula" maxlength="2" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">Número Máximo de Reprovações:</th>
					<td><h:inputText value="#{parametros.obj.maxReprovacoes}" size="3" 
						onkeyup="return formatarInteiro(this);" id="maxReprovacoes" maxlength="2" /></td>
				</tr>
				<tr><td colspan="2" class="subFormulario">Parâmetros Curriculares</td></tr>
				<tr>
					<th class="obrigatorio">Número Mínimo de Créditos de Extra-Curricular:</th>
					<td><h:inputText value="#{parametros.obj.minCreditosExtra}" size="3" 
						onkeyup="return formatarInteiro(this);" id="minCreditosExtra" maxlength="2" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">Número Máximo de Créditos de Extra-Curricular:</th>
					<td><h:inputText value="#{parametros.obj.maxCreditosExtra}" size="3" 
						onkeyup="return formatarInteiro(this);" id="maxCreditosExtra" maxlength="2" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">Equivalência de Crédito e Hora/Aula:</th>
					<td><h:inputText value="#{parametros.obj.horasCreditosAula}" size="3" 
						onkeyup="return formatarInteiro(this);" id="horasCreditosaula" maxlength="2" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">Equivalência de Crédito e Hora/Laboratório:</th>
					<td><h:inputText value="#{parametros.obj.horasCreditosLaboratorio}" size="3" 
						onkeyup="return formatarInteiro(this);"	id="horasCreditoslab" maxlength="2" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">Equivalência de Crédito e Hora/Estágio:</th>
					<td><h:inputText value="#{parametros.obj.horasCreditosEstagio}" size="3" 
						onkeyup="return formatarInteiro(this);"	id="horasCreditosest" maxlength="2" /></td>
				</tr>
				<tr><td colspan="2" class="subFormulario">Cadastro de Turmas</td></tr>
				<tr>
					<th>Verificar Choque de Horários em Cadastro de Turmas:</th>
					<td><h:selectBooleanCheckbox value="#{parametros.obj.impedeChoqueHorarios}" id="impedeChoqueHorarios" /></td>
				</tr>
				<tr>
					<th>Permite CH Compartilhada entre Docentes em uma Turma:</th>
					<td><h:selectBooleanCheckbox value="#{parametros.obj.permiteChCompartilhada}" id="permiteChCompartilhada" />
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">CH Total Máxima do Componente Curricular para Turmas de Férias:</th>
					<td><h:inputText value="#{parametros.obj.chMaximaTurmaFerias}" size="4" 
						onkeyup="return formatarInteiro(this);"	id="chMaximaTurmaFerias" maxlength="3" /></td>
				</tr>
				<tr><td colspan="2" class="subFormulario">Avaliação</td></tr>
				<tr>
					<th width="50%" class="obrigatorio">Método de Avaliação:</th>
					<td>
						<h:selectOneMenu value="#{parametros.obj.metodoAvaliacao}" id="metodoAvaliacao" 
								valueChangeListener="#{parametros.changeMetodoAvaliacao}" onchange="submit();">
							<f:selectItems value="#{ parametros.metodosAvaliacao }"/>
							<a4j:support event="onchange" reRender="mediaMinimaAprovacao, mediaMinimaAprovacaoCombo, mediaMinimaPossibilitaRec, mediaMinimaPossibilitaRecCombo, mediaMinimaAprovacaoPorMedia, mediaMinimaAprovacaoPorMediaCombo"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<t:htmlTag value="tr" rendered="#{!parametros.competencia}">
					<th class="obrigatorio">Média Mínima de Aprovação:</th>
					<td>
						<h:inputText value="#{parametros.obj.mediaMinimaAprovacao}" size="3" rendered="#{parametros.nota}"
							onkeypress="return(formatarMascara(this,event,'#.#'))" id="mediaMinimaAprovacao" maxlength="3" />
						<h:selectOneMenu value="#{parametros.obj.mediaMinimaAprovacao}" id="mediaMinimaAprovacaoCombo" rendered="#{parametros.conceito}">
							<f:selectItems value="#{ parametros.conceitosCombo }"/>
						</h:selectOneMenu>
					</td>
				</t:htmlTag>
				<t:htmlTag value="tr" rendered="#{!parametros.competencia}">
					<th class="obrigatorio">Média Mínima que possibilita recuperação:</th>
					<td>
						<h:inputText value="#{parametros.obj.mediaMinimaPossibilitaRecuperacao}" size="3" rendered="#{parametros.nota}"
							onkeypress="return(formatarMascara(this,event,'#.#'))" id="mediaMinimaPossibilitaRec" maxlength="3" />
						<h:selectOneMenu value="#{parametros.obj.mediaMinimaPossibilitaRecuperacao}" id="mediaMinimaPossibilitaRecCombo" rendered="#{parametros.conceito}">
							<f:selectItems value="#{ parametros.conceitosCombo }"/>
						</h:selectOneMenu>
					</td>
				</t:htmlTag>
				<t:htmlTag value="tr" rendered="#{!parametros.competencia}">
					<th class="obrigatorio">Média Mínima de Aprovação para passar por média:</th>
					<td>
						<h:inputText value="#{parametros.obj.mediaMinimaPassarPorMedia}" size="3" rendered="#{parametros.nota}"
							onkeypress="return(formatarMascara(this,event,'#.#'))" id="mediaMinimaAprovacaoPorMedia" maxlength="3" />
						<h:selectOneMenu value="#{parametros.obj.mediaMinimaPassarPorMedia}" id="mediaMinimaAprovacaoPorMediaCombo" rendered="#{parametros.conceito}">
							<f:selectItems value="#{ parametros.conceitosCombo }"/>
						</h:selectOneMenu>
						<ufrn:help img="/img/ajuda.gif">Caso esta média não seja alcançada vai ser necessário uma outra avaliação.</ufrn:help>
					</td>
				</t:htmlTag>
				<tr>
					<th class="obrigatorio">Frequência Mínima para Aprovação:</th>
					<td><h:inputText value="#{parametros.obj.frequenciaMinima}" size="5"
						onkeyup="return formatarInteiro(this);" id="frequenciaMinima" maxlength="3" />
						<ufrn:help img="/img/ajuda.gif">Valor em porcentagem. Por exemplo, se este valor for definido com 70 e durante o período tiver 100 aulas, o discente para não ser reprovado por falta deverá comparecer a no mínimo 70 aulas.</ufrn:help>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Número Máximo de Avaliações por Turma:</th>
					<td><h:inputText value="#{parametros.obj.qtdAvaliacoes}" size="2"
						onkeyup="return formatarInteiro(this);" id="qtdAvaliacoes" maxlength="1" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">Pesos das Avaliações:</th>
					<td><h:inputText value="#{parametros.obj.pesosAvaliacoes}" size="18"
						onkeypress="return(formatarMascara(this,event,'#,#,#,#,#,#,#,#,#'))" id="pesos" maxlength="17" />
					<ufrn:help img="/img/ajuda.gif">Informe os pesos aplicados em cada avaliação, separados por vírgula. Exemplo de preenchimento: 4,5,6</ufrn:help>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Peso da Média e Peso da Recuperação:</th>
					<td><h:inputText value="#{parametros.obj.pesoMediaRecuperacao}" size="5"
						onkeypress="return(formatarMascara(this,event,'##,##'))" id="pesoMediaRec" maxlength="5" />
					<ufrn:help img="/img/ajuda.gif">
					
					Pesos separados por vírgulas. Ex: 10,30 (peso da média =  10 e  peso da recuperação = 30).
					Utilizado para poder dar pesos específicos para a média sem recuperação(MediaSemRec) 
					e para a recuperação(Rec) no cálculo da média final(MF). Por exemplo, em uma IFES ou Unidade 
					a MediaSemRec e a Rec podem ter pesos iguais para ambas as notas. 
					Por exemplo, ambas com peso 10. Logo, o cálculo da MF seria feito assim: MF = ( ( 10*MediaSemRec + 10*Rec ) / ( 10 + 10 ) ). 
					Mas em outra IFES ou Unidade o peso da recuperação pode ser menor ou maior! 
					Por exemplo, suponha que os pesos da mediaSemRec e peso da Rec sejam respectivamente 70 e 30. 
					O cálculo seria feito assim: MF =( ( 70*MediaSemRec + 30*Rec ) / ( 70 + 30 ) ). 
					O primeiro valor do texto (antes da vírgula) será o peso da MediaSemRec e o segundo valor será a o peso da Rec.
					
					</ufrn:help>
					</td>
				</tr>
				<tr><td colspan="2" class="subFormulario">Outros Parâmetros</td></tr>
				<tr>
					<th>Solicitar aos discentes a atualização de seus dados pessoais a cada período de matrícula:</th>
					<td><h:selectBooleanCheckbox value="#{parametros.obj.solicitarAtualizacaoDadosMatricula}" id="solicitarAtualizacaoDadosMatricula" />
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Quantidade de períodos regulares:</th>
					<td><h:inputText value="#{parametros.obj.quantidadePeriodosRegulares}" size="1" onkeyup="return formatarInteiro(this);" id="quantidadePeriodosRegulares" maxlength="1" /></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
					<h:commandButton value="Confirmar Parâmetros" action="#{parametros.confirmar}" id="btnConfirmar"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{parametros.cancelar}" id="cancelar" immediate="true"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>