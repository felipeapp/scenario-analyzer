<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="title"><ufrn:subSistema /> > Par�metros do Sistema</h2>
	<div class="descricaoOperacao">
		<p><b>Caro usu�rio,</b></p>
		<p>Informe, a seguir, os par�metros utilizados na unidade acad�mica abaixo detalhada.</p>
	</div>
	<table class="visualizacao">
		<tr>
			<th width="20%">Unidade Respons�vel:</th>
			<td>${parametros.obj.unidade.nome }</td>
		</tr>
		<tr>
			<th>N�vel de Ensino:</th>
			<td>${parametros.nivelDescricao}</td>
		</tr>
	</table>
	<br/>
	<h:form id="form">
		<table class="formulario" width="100%">
			<caption>Defina os Valores dos Par�metros</caption>
			<tbody>
				<tr><td colspan="2" class="subFormulario">Matr�cula, Trancamentos e Reprova��o em Componentes Curriculares</td></tr>
				<tr>
					<th class="obrigatorio">N�mero M�ximo de Reprova��es:</th>
					<td><h:inputText value="#{parametros.obj.maxReprovacoes}" size="3" 
						onkeyup="return formatarInteiro(this);" id="maxReprovacoes" maxlength="2" /></td>
				</tr>
				<tr><td colspan="2" class="subFormulario">Par�metros Curriculares</td></tr>
				<tr>
					<th class="obrigatorio">Equival�ncia de Cr�dito e Hora/Aula:</th>
					<td><h:inputText value="#{parametros.obj.horasCreditosAula}" size="3" 
						onkeyup="return formatarInteiro(this);" id="horasCreditosaula" maxlength="2" />
						<ufrn:help>Valor de hora/aula equivalente a 1 unidade de cr�dito.</ufrn:help>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Equival�ncia de Cr�dito e Hora/Laborat�rio:</th>
					<td><h:inputText value="#{parametros.obj.horasCreditosLaboratorio}" size="3" 
						onkeyup="return formatarInteiro(this);"	id="horasCreditoslab" maxlength="2" />
						<ufrn:help>Valor de hora/aula de laborat�rio equivalente a 1 unidade de cr�dito.</ufrn:help>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Equival�ncia de Cr�dito e Hora/Est�gio:</th>
					<td><h:inputText value="#{parametros.obj.horasCreditosEstagio}" size="3" 
						onkeyup="return formatarInteiro(this);"	id="horasCreditosest" maxlength="2" />
						<ufrn:help>Valor de hora/aula de est�gio equivalente a 1 unidade de cr�dito.</ufrn:help>
					</td>
				</tr>
				<tr><td colspan="2" class="subFormulario">Cadastro de Turmas</td></tr>
				<tr>
					<th>Verificar Choque de Hor�rios em Cadastro de Turmas e Disciplinas:</th>
					<td><h:selectBooleanCheckbox value="#{parametros.obj.impedeChoqueHorarios}" id="impedeChoqueHorarios" /></td>
				</tr>
				<tr>
					<th>Permite CH Compartilhada entre Docentes em uma Disciplina:</th>
					<td><h:selectBooleanCheckbox value="#{parametros.obj.permiteChCompartilhada}" id="permiteChCompartilhada" />
					</td>
				</tr>
	
				<tr><td colspan="2" class="subFormulario">Avalia��o</td></tr>
				<tr>
					<th width="50%" class="obrigatorio">M�todo de Avalia��o:</th>
					<td>
						<h:selectOneMenu value="#{parametros.obj.metodoAvaliacao}" id="metodoAvaliacao" 
								valueChangeListener="#{parametros.changeMetodoAvaliacao}" onchange="submit();">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{ parametros.metodosAvaliacaoMedio }"/>
							<a4j:support event="onchange" reRender="mediaMinimaAprovacao, mediaMinimaAprovacaoCombo, mediaMinimaPossibilitaRec, mediaMinimaPossibilitaRecCombo, mediaMinimaAprovacaoPorMedia, mediaMinimaAprovacaoPorMediaCombo"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<t:htmlTag value="tr" rendered="#{!parametros.competencia}">
					<th class="obrigatorio">M�dia M�nima de Aprova��o:</th>
					<td>
						<h:inputText value="#{parametros.obj.mediaMinimaAprovacao}" size="3" rendered="#{parametros.nota}"
							onkeypress="return(formatarMascara(this,event,'#.#'))" id="mediaMinimaAprovacao" maxlength="3" />
						<h:selectOneMenu value="#{parametros.obj.mediaMinimaAprovacao}" id="mediaMinimaAprovacaoCombo" rendered="#{parametros.conceito}">
							<f:selectItems value="#{ parametros.conceitosCombo }"/>
						</h:selectOneMenu>
					</td>
				</t:htmlTag>
				<t:htmlTag value="tr" rendered="#{!parametros.competencia}">
					<th class="obrigatorio">M�dia M�nima que possibilita recupera��o:</th>
					<td>
						<h:inputText value="#{parametros.obj.mediaMinimaPossibilitaRecuperacao}" size="3" rendered="#{parametros.nota}"
							onkeypress="return(formatarMascara(this,event,'#.#'))" id="mediaMinimaPossibilitaRec" maxlength="3" />
						<h:selectOneMenu value="#{parametros.obj.mediaMinimaPossibilitaRecuperacao}" id="mediaMinimaPossibilitaRecCombo" rendered="#{parametros.conceito}">
							<f:selectItems value="#{ parametros.conceitosCombo }"/>
						</h:selectOneMenu>
					</td>
				</t:htmlTag>
				<t:htmlTag value="tr" rendered="#{!parametros.competencia}">
					<th class="obrigatorio">M�dia M�nima de Aprova��o para passar por m�dia:</th>
					<td>
						<h:inputText value="#{parametros.obj.mediaMinimaPassarPorMedia}" size="3" rendered="#{parametros.nota}"
							onkeypress="return(formatarMascara(this,event,'#.#'))" id="mediaMinimaAprovacaoPorMedia" maxlength="3" />
						<h:selectOneMenu value="#{parametros.obj.mediaMinimaPassarPorMedia}" id="mediaMinimaAprovacaoPorMediaCombo" rendered="#{parametros.conceito}">
							<f:selectItems value="#{ parametros.conceitosCombo }"/>
						</h:selectOneMenu>
						<ufrn:help img="/img/ajuda.gif">Caso esta m�dia n�o seja alcan�ada vai ser necess�rio uma outra avalia��o.</ufrn:help>
					</td>
				</t:htmlTag>
				<tr>
					<th class="obrigatorio">Frequ�ncia M�nima para Aprova��o:</th>
					<td><h:inputText value="#{parametros.obj.frequenciaMinima}" size="5"
						onkeyup="return formatarInteiro(this);" id="frequenciaMinima" maxlength="3" />
						<ufrn:help img="/img/ajuda.gif">Valor em porcentagem. Por exemplo, se este valor for definido com 70 e durante o per�odo tiver 100 aulas, o discente para n�o ser reprovado por falta dever� comparecer a no m�nimo 70 aulas.</ufrn:help>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">N�mero M�ximo de Avalia��es por Disciplina:</th>
					<td><h:inputText value="#{parametros.obj.qtdAvaliacoes}" size="2"
						onkeyup="return formatarInteiro(this);" id="qtdAvaliacoes" maxlength="1" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">Pesos das Avalia��es:</th>
					<td><h:inputText value="#{parametros.obj.pesosAvaliacoes}" size="18"
						onkeypress="return(formatarMascara(this,event,'#,#,#,#,#,#,#,#,#'))" id="pesos" maxlength="17" />
					<ufrn:help img="/img/ajuda.gif">Informe os pesos aplicados em cada avalia��o, separados por v�rgula. Exemplo de preenchimento: 4,5,6</ufrn:help>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Peso da M�dia e Peso da Recupera��o:</th>
					<td><h:inputText value="#{parametros.obj.pesoMediaRecuperacao}" size="5"
						onkeypress="return(formatarMascara(this,event,'##,##'))" id="pesoMediaRec" maxlength="5" />
					<ufrn:help img="/img/ajuda.gif">
					
					Pesos separados por v�rgulas. Ex: 10,30 (peso da m�dia =  10 e  peso da recupera��o = 30).
					Utilizado para poder dar pesos espec�ficos para a m�dia sem recupera��o(MediaSemRec) 
					e para a recupera��o(Rec) no c�lculo da m�dia final(MF). Por exemplo, em uma IFES ou Unidade 
					a MediaSemRec e a Rec podem ter pesos iguais para ambas as notas. 
					Por exemplo, ambas com peso 10. Logo, o c�lculo da MF seria feito assim: MF = ( ( 10*MediaSemRec + 10*Rec ) / ( 10 + 10 ) ). 
					Mas em outra IFES ou Unidade o peso da recupera��o pode ser menor ou maior! 
					Por exemplo, suponha que os pesos da mediaSemRec e peso da Rec sejam respectivamente 70 e 30. 
					O c�lculo seria feito assim: MF =( ( 70*MediaSemRec + 30*Rec ) / ( 70 + 30 ) ). 
					O primeiro valor do texto (antes da v�rgula) ser� o peso da MediaSemRec e o segundo valor ser� a o peso da Rec.
					
					</ufrn:help>
					</td>
				</tr>
				<tr><td colspan="2" class="subFormulario">Outros Par�metros</td></tr>
				<tr>
					<th>Solicitar aos discentes a atualiza��o de seus dados pessoais a cada per�odo de matr�cula:</th>
					<td><h:selectBooleanCheckbox value="#{parametros.obj.solicitarAtualizacaoDadosMatricula}" id="solicitarAtualizacaoDadosMatricula" />
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
					<h:commandButton value="Confirmar Par�metros" action="#{parametros.confirmar}" id="btnConfirmar"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{parametros.cancelar}" id="cancelar" immediate="true"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>