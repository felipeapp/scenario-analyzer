<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2> <ufrn:subSistema /> > Mesário Voluntário</h2>


	<div class="descricaoOperacao">
		<p>
			Esta operação é destinada a inscrição de estudantes universitários, matriculados na instituição, 
			para participar do Projeto <b>Mesário Universitário</b>.
		</p>
		<p>
			O projeto possibilita registrar a participação no mesmo, como atividade extracurricular 
			para os universitários o total de horas/aula estipulado conforme as seguintes condições não cumulativas entre si:
		</p>
		<br/>
			<div>
				<p align="justify"><dd> 
					<b>1.</b> O aluno será beneficiado com o total de <b>50 h/aula</b> de atividade extracurricular, 
					sendo <b>10 h/aula</b> em razão da participação nos treinamentos teóricos e prático com urna
					eletrônica, <b>20 h/aula</b> em face do trabalho como mesário no <u>1º turno de votação</u> e <b>20 h/aula</b>
					pelo desenvolvimento das funções de mesário no <u>2º turno de votação</u>;
				</dd></p>
				<p><dd> 
					<b>2.</b> O participante terá direito somente a <b>40 h/aula</b> de atividade  extracurricular, sendo
					<b>20 h/aula</b> em razão do trabalho como mesário no <u>1º turno de votação</u> e <b>20 h/aula</b> 
					em face do desenvolvimento das funções de mesário no <u>2º turno de votação</u>, em bem assim,
					não tenha participado dos treinamentos teórico e prático com urna eletrônica.
				</dd>
				</p>
				<p><dd> 
					<b>3.</b> O aluno fará jus somente a <b>30 h/aula</b> de atividade extracurricular, sendo <b>10 h/aula</b> em
					razão da participação nos treinamentos teórico e prático com urna eletrônica, e
					<b>20 h/aula</b> em face do trabalho como mesário apenas durante <u>um dos turnos de votação (1º ou 2º)</u>;
				</dd></p>
				<p><dd>
					<b>4.</b> O beneficiário receberá somente a <b>20 h/aula</b> de atividade extracurricular se trabalhou 
					como mesário <u>apenas um dos turnos de votação (1º ou 2º)</u> e não assistiu aos treinamentos teórico 
					e prático com urna eletrônica.
				</dd></p>
				<p><dd>
					<b>5.</b> Se o participante apenas assistir aos treinamentos teórico e prático com urna eletrônica, porém, 
					sem participação no trabalho como mesário, em quaisquer dos turnos de votação, <b>não fará jus às 10 h/aula 
					em razão dos referidos treinamentos.</b>
				</dd></p>
			</div>	
		
	</div>


	<h:form>
	<table class="formulario" width="70%">
		<caption class="listagem">Cadastro de Mesário Voluntário</caption>
		<h:inputHidden value="#{mesarioVoluntarioMBean.confirmButton}" />
		<h:inputHidden value="#{mesarioVoluntarioMBean.obj.id}" />
		
		<tr>
			<th><b>Discente:</b></th>
			<td>
				<h:outputText value="#{mesarioVoluntarioMBean.obj.discenteInscricao.matriculaNomeNivel}" />
			</td>
		</tr>
		<tr>
			<th class="obrigatorio">Título de Eleitor:</th>
			<td><h:inputText value="#{mesarioVoluntarioMBean.obj.tituloEleitor}" id="tituloEleitor" size="30" maxlength="40" /></td>
		</tr>
		<tr>
			<th class="obrigatorio">Zona Eleitoral:</th>
			<td><h:inputText value="#{mesarioVoluntarioMBean.obj.zona}" id="zona" size="10" maxlength="10" /></td>
		</tr>
		<tr>
			<th class="obrigatorio">Seção Eleitoral:</th>
			<td><h:inputText value="#{mesarioVoluntarioMBean.obj.secao}" id="secao" size="30" maxlength="40" /></td>
		</tr>
		
		<tfoot>
			<tr>
				<td colspan=2>
					<h:commandButton value="#{mesarioVoluntarioMBean.confirmButton}" action="#{mesarioVoluntarioMBean.cadastrar}" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{mesarioVoluntarioMBean.cancelar}" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
		<br>
	<center><h:graphicImage url="/img/required.gif"/> 
		<span class="fontePequena"> Campos de preenchimento obrigatório.</span>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>