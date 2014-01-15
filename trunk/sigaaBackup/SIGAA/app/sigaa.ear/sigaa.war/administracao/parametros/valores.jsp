<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="title"><ufrn:subSistema /> > Parâmetros do Sistema</h2>
	<div class="descricaoOperacao">
		<p><b>Caro usuário,</b></p>
		<p>Informe, a seguir, os parâmetros utilizados na unidade acadêmica abaixo detalhada.</p>
	</div>
	<table class="visualizacao">
		<c:if test="${parametros.obj.unidade.id > 0}">
		<tr>
			<th width="20%">Unidade Responsável:</th>
			<td>${parametros.obj.unidade.nome }</td>
		</tr>
		</c:if>
		<c:if test="${parametros.obj.nivelDescr != 'Desconhecido'}">
		<tr>
			<th>Nível de Ensino:</th>
			<td>${parametros.obj.nivelDescr}</td>
		</tr>
		</c:if>
		<c:if test="${parametros.obj.modalidade.id > 0}">
			<tr>
				<th>Modalidade de Ensino:</th>
				<td>${parametros.obj.modalidade.descricao}</td>
			</tr>
		</c:if>
		<c:if test="${parametros.obj.convenio.id > 0}">
			<tr>
				<th>Convênio Acadêmico:</th>
				<td>${parametros.obj.convenio.descricao }</td>
			</tr>
		</c:if>
		<c:if test="${parametros.obj.curso.id > 0}">
			<tr>
				<th>Curso:</th>
				<td>${parametros.obj.curso.descricaoCompleta}</td>
			</tr>
		</c:if>
	</table>
<br>
	<h:form id="altForm">
		<table class="formulario" width="100%">
			<caption>Defina os Valores dos Parâmetros</caption>
			<tbody>
				<tr><td colspan="2" class="subFormulario">Aluno Especial</td></tr>
				<tr>
					<th>Número Máximo de Disciplinas por período cursadas por Aluno Especial:</th>
					<td><h:inputText value="#{parametros.obj.maxDisciplinasAlunoEspecial}" size="3" 
						onkeyup="return formatarInteiro(this);" id="maxDisciplinasAlunoEspecial" maxlength="2" /></td>
				</tr>
				<tr>
					<th>Número Máximo de Períodos consecutivos ou não que um Aluno Especial pode cursar:</th>
					<td><h:inputText value="#{parametros.obj.maxPeriodosAlunoEspecial}" size="3" 
						onkeyup="return formatarInteiro(this);" id="maxPeriodosAlunoEspecial" maxlength="2" /></td>
				</tr>
				<tr><td colspan="2" class="subFormulario">Matrícula, Trancamentos e Reprovação em Componentes Curriculares</td></tr>
				<tr>
					<th>Percentual Máximo Cumprido para Permitir Trancamento:</th>
					<td><h:inputText value="#{parametros.obj.percentualMaximoCumpridoTrancamento}" size="6" 
							onkeyup="return formatarInteiro(this);" id="percentualMaximoCumpridoTrancamento" maxlength="3">						
						</h:inputText>
						<ufrn:help>O valor informado, entre 0 e 100, é o percentual do período da turma cumprida pelo discente para poder permitir trancamento.</ufrn:help>
					</td>
				</tr>
				<tr>
					<th>Número Máximo de Trancamentos de PROGRAMA:</th>
					<td><h:inputText value="#{parametros.obj.maxTrancamentos}" size="3" 
						onkeyup="return formatarInteiro(this);" id="maxTrancamentos" maxlength="2" /></td>
				</tr>
				<tr>
					<th>Número Máximo de Trancamentos de MATRÍCULA:</th>
					<td><h:inputText value="#{parametros.obj.maxTrancamentosMatricula}" size="3" 
						onkeyup="return formatarInteiro(this);" id="maxTrancamentosMatricula" maxlength="2" /></td>
				</tr>
				<tr>
					<th>Número Máximo de Reprovações:</th>
					<td><h:inputText value="#{parametros.obj.maxReprovacoes}" size="3" 
						onkeyup="return formatarInteiro(this);" id="maxReprovacoes" maxlength="2" /></td>
				</tr>
				<tr><td colspan="2" class="subFormulario">Parâmetros Curriculares</td></tr>
				<tr>
					<th>Número Mínimo de Créditos de Extra-Curricular:</th>
					<td><h:inputText value="#{parametros.obj.minCreditosExtra}" size="3" 
						onkeyup="return formatarInteiro(this);" id="minCreditosExtra" maxlength="2" /></td>
				</tr>
				<tr>
					<th>Número Máximo de Créditos de Extra-Curricular:</th>
					<td><h:inputText value="#{parametros.obj.maxCreditosExtra}" size="3" 
						onkeyup="return formatarInteiro(this);" id="maxCreditosExtra" maxlength="2" /></td>
				</tr>
				<tr>
					<th>Equivalência de Crédito e Hora/Aula:</th>
					<td><h:inputText value="#{parametros.obj.horasCreditosAula}" size="3" 
						onkeyup="return formatarInteiro(this);" id="horasCreditosaula" maxlength="2" /></td>
				</tr>
				<tr>
					<th>Equivalência de Crédito e Hora/Laboratório:</th>
					<td><h:inputText value="#{parametros.obj.horasCreditosLaboratorio}" size="3" 
						onkeyup="return formatarInteiro(this);"	id="horasCreditoslab" maxlength="2" /></td>
				</tr>
				<tr>
					<th>Equivalência de Crédito e Hora/Estágio:</th>
					<td><h:inputText value="#{parametros.obj.horasCreditosEstagio}" size="3" 
						onkeyup="return formatarInteiro(this);"	id="horasCreditosest" maxlength="2" /></td>
				</tr>
				<tr>
					<th>Duração de uma Aula Regular:</th>
					<td>
						<h:inputText value="#{parametros.obj.minutosAulaRegular}" size="3" 
						onkeyup="return formatarInteiro(this);"	id="minutosAulaRegular" maxlength="2" />
						<ufrn:help>Tempo, em minutos, de duração de uma aula regular. Ex.: 50, 45, etc. Este parâmetro é utilizado no cálculo do número de aulas no período letivo.</ufrn:help>
					</td>
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
					<th>CH Total Máxima do Componente Curricular para Turmas de Férias:</th>
					<td><h:inputText value="#{parametros.obj.chMaximaTurmaFerias}" size="4" 
						onkeyup="return formatarInteiro(this);"	id="chMaximaTurmaFerias" maxlength="3" /></td>
				</tr>
				<tr><td colspan="2" class="subFormulario">Avaliação</td></tr>
				<tr>
					<th width="50%">Método de Avaliação:</th>
					<td>
						<h:selectOneMenu value="#{parametros.obj.metodoAvaliacao}" id="metodoAvaliacao">
						<f:selectItems value="#{ parametros.metodosAvaliacao }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>Média Mínima de Aprovação:</th>
					<td><h:inputText value="#{parametros.obj.mediaMinimaAprovacao}" size="3"
						onkeypress="return(formatarMascara(this,event,'#.#'))" id="mediaMinimaAprovacao" maxlength="3" />
					</td>
				</tr>
				<tr>
					<th>Média Mínima que possibilita recuperação:</th>
					<td><h:inputText value="#{parametros.obj.mediaMinimaPossibilitaRecuperacao}" size="3"
						onkeypress="return(formatarMascara(this,event,'#.#'))" id="mediaMinimaPossibilitaRec" maxlength="3" />
					</td>
				</tr>
				<tr>
					<th>Média Mínima de Aprovação para passar por média:</th>
					<td><h:inputText value="#{parametros.obj.mediaMinimaPassarPorMedia}" size="3"
						onkeypress="return(formatarMascara(this,event,'#.#'))" id="mediaMinimaAprovacaoPorMedia" maxlength="3" />
						<ufrn:help img="/img/ajuda.gif">Caso esta média não seja alcançada vai ser necessário uma outra avaliação.</ufrn:help>
					</td>
				</tr>
				<tr>
					<th>Frequência Mínima para Aprovação:</th>
					<td><h:inputText value="#{parametros.obj.frequenciaMinima}" size="5"
						onkeydown="return formatarInteiro(this);" id="frequenciaMinima" maxlength="4" />
						<ufrn:help img="/img/ajuda.gif">Valor em porcentagem. Por exemplo, se este valor for definido com 70 e durante o período tiver 100 aulas, o discente para não ser reprovado por falta deverá comparecer a no mínimo 70 aulas.</ufrn:help>
					</td>
				</tr>
				<tr>
					<th>Número Máximo de Avaliações por Turma:</th>
					<td><h:inputText value="#{parametros.obj.qtdAvaliacoes}" size="3"
						onkeyup="return formatarInteiro(this);" id="qtdAvaliacoes" maxlength="2" /></td>
				</tr>
				<tr>
					<th>Pesos das Avaliações:</th>
					<td><h:inputText value="#{parametros.obj.pesosAvaliacoes}" size="10"
						onkeypress="return(formatarMascara(this,event,'#,#,#,#,#'))" id="pesos" maxlength="9" />
					<ufrn:help img="/img/ajuda.gif">Informe os pesos aplicados em cada avaliação, separados por vírgula. Exemplo de preenchimento: 4,5,6</ufrn:help>
					</td>
				</tr>
				<tr>
					<th>Pesos das Avaliações com 2 Unidade:</th>
					<td><h:inputText value="#{parametros.obj.pesosAvaliacoes2Unidades}" size="10"
						onkeypress="return(formatarMascara(this,event,'#,#,#,#,#'))" id="pesos2Unidades" maxlength="9" />
					<ufrn:help img="/img/ajuda.gif">
					
						Caso exista turmas com quantidades de avaliações diferentes. Por exemplo, quando as disciplinas podem ser configuradas 
						para terem 2 ou 3 unidades. Informe os pesos aplicados em cada avaliação para as turmas com 2 unidades. Exemplo de 
						preenchimento: 5,6
						
					</ufrn:help>
					</td>
				</tr>
				<tr>
					<th>Peso da Média e Peso da Recuperação:</th>
					<td><h:inputText value="#{parametros.obj.pesoMediaRecuperacao}" size="5"
						onkeypress="return(formatarMascara(this,event,'##,##'))" id="pesoMediaRec" maxlength="5" />
					<ufrn:help img="/img/ajuda.gif">
					
					Utilizado para poder dar pesos específicos para a média sem recuperação(MediaSemRec) e 
	 				para a recuperação(Rec) no calculo da média final(MF). Por exemplo, em uma IFES ou Unidade a MediaSemRec e a Rec podem ter
	 				pesos iguais para ambas as notas, por exemplo ambas com peso 10, logo o cálculo da MF seria
	 				feito assim: MF = ( ( 10*MediaSemRec + 10*Rec ) / ( 10 + 10 ) ).
	 				Mas em outra IFES ou Unidade o peso da recuperação pode ser menor ou maior! 
	 				Por exemplo, suponha que os pesos da mediaSemRec e peso da Rec sejam respectivamente 70 e 30. 
	 				O calculo seria feito assim: MF =( ( 70*MediaSemRec + 30*Rec ) / ( 70 + 30 ) ).
	 				O primeiro inteiro da String será o peso da MediaSemRec e o segundo inteiro será a o peso da Rec.
					
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
					<th>Arquivo do Certificado:</th>
					<td><h:inputText value="#{parametros.obj.certificado}" size="30" onkeyup="return formatarInteiro(this);" maxlength="30" id="certificado" /></td>
				</tr>
				<tr>
					<th>Início da Faixa de Matrícula:</th>
					<td><h:inputText value="#{parametros.obj.inicioFaixaMatricula}" size="6" onkeyup="return formatarInteiro(this);" id="inicioFaixaMatricula" maxlength="5" /></td>
				</tr>
				<tr>
					<th>Quantidade de períodos regulares:</th>
					<td><h:inputText value="#{parametros.obj.quantidadePeriodosRegulares}" size="1" onkeyup="return formatarInteiro(this);" id="quantidadePeriodosRegulares" maxlength="1" /></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
					<h:commandButton value="Confirmar Parâmetros" action="#{parametros.confirmar}" id="btnConfirmar"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{parametros.cancelar}" id="cancelar" />
					<h:commandButton action="#{parametros.iniciar}" id="voltar" value="<< Voltar" rendered="#{parametros.habilitarVoltar }" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>