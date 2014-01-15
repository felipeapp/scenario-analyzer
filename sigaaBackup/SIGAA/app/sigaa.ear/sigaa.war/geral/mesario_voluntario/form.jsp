<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2> <ufrn:subSistema /> > Mes�rio Volunt�rio</h2>


	<div class="descricaoOperacao">
		<p>
			Esta opera��o � destinada a inscri��o de estudantes universit�rios, matriculados na institui��o, 
			para participar do Projeto <b>Mes�rio Universit�rio</b>.
		</p>
		<p>
			O projeto possibilita registrar a participa��o no mesmo, como atividade extracurricular 
			para os universit�rios o total de horas/aula estipulado conforme as seguintes condi��es n�o cumulativas entre si:
		</p>
		<br/>
			<div>
				<p align="justify"><dd> 
					<b>1.</b> O aluno ser� beneficiado com o total de <b>50 h/aula</b> de atividade extracurricular, 
					sendo <b>10 h/aula</b> em raz�o da participa��o nos treinamentos te�ricos e pr�tico com urna
					eletr�nica, <b>20 h/aula</b> em face do trabalho como mes�rio no <u>1� turno de vota��o</u> e <b>20 h/aula</b>
					pelo desenvolvimento das fun��es de mes�rio no <u>2� turno de vota��o</u>;
				</dd></p>
				<p><dd> 
					<b>2.</b> O participante ter� direito somente a <b>40 h/aula</b> de atividade  extracurricular, sendo
					<b>20 h/aula</b> em raz�o do trabalho como mes�rio no <u>1� turno de vota��o</u> e <b>20 h/aula</b> 
					em face do desenvolvimento das fun��es de mes�rio no <u>2� turno de vota��o</u>, em bem assim,
					n�o tenha participado dos treinamentos te�rico e pr�tico com urna eletr�nica.
				</dd>
				</p>
				<p><dd> 
					<b>3.</b> O aluno far� jus somente a <b>30 h/aula</b> de atividade extracurricular, sendo <b>10 h/aula</b> em
					raz�o da participa��o nos treinamentos te�rico e pr�tico com urna eletr�nica, e
					<b>20 h/aula</b> em face do trabalho como mes�rio apenas durante <u>um dos turnos de vota��o (1� ou 2�)</u>;
				</dd></p>
				<p><dd>
					<b>4.</b> O benefici�rio receber� somente a <b>20 h/aula</b> de atividade extracurricular se trabalhou 
					como mes�rio <u>apenas um dos turnos de vota��o (1� ou 2�)</u> e n�o assistiu aos treinamentos te�rico 
					e pr�tico com urna eletr�nica.
				</dd></p>
				<p><dd>
					<b>5.</b> Se o participante apenas assistir aos treinamentos te�rico e pr�tico com urna eletr�nica, por�m, 
					sem participa��o no trabalho como mes�rio, em quaisquer dos turnos de vota��o, <b>n�o far� jus �s 10 h/aula 
					em raz�o dos referidos treinamentos.</b>
				</dd></p>
			</div>	
		
	</div>


	<h:form>
	<table class="formulario" width="70%">
		<caption class="listagem">Cadastro de Mes�rio Volunt�rio</caption>
		<h:inputHidden value="#{mesarioVoluntarioMBean.confirmButton}" />
		<h:inputHidden value="#{mesarioVoluntarioMBean.obj.id}" />
		
		<tr>
			<th><b>Discente:</b></th>
			<td>
				<h:outputText value="#{mesarioVoluntarioMBean.obj.discenteInscricao.matriculaNomeNivel}" />
			</td>
		</tr>
		<tr>
			<th class="obrigatorio">T�tulo de Eleitor:</th>
			<td><h:inputText value="#{mesarioVoluntarioMBean.obj.tituloEleitor}" id="tituloEleitor" size="30" maxlength="40" /></td>
		</tr>
		<tr>
			<th class="obrigatorio">Zona Eleitoral:</th>
			<td><h:inputText value="#{mesarioVoluntarioMBean.obj.zona}" id="zona" size="10" maxlength="10" /></td>
		</tr>
		<tr>
			<th class="obrigatorio">Se��o Eleitoral:</th>
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
		<span class="fontePequena"> Campos de preenchimento obrigat�rio.</span>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>