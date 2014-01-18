<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<a4j:keepAlive beanName="convenioEstagioMBean" />

	<style>
.textoConteudo {
	line-height: 150%;
	font-family: serif;
	font-size: 12pt;
	padding-top: 150px;
	text-align: justify;
	text-indent: 25px;
}

.texto_direita {
	width: 300px;
	height: 100px;
	font-family: serif;
	float: right;
	font-size: 12pt;
	position: relative;
	text-align: justify;
}

.clausula{
	text-indent: 0px;
	font-weight: bold;
}

.subClausula{
	text-indent: 0px;
}
.item{
	text-indent: 35px;
}
.subItem{
	text-indent: 50px;
}
</style>

	<br />
	<div class="texto_direita">
		<p>
			CONV�NIO <b>N&deg; ${convenioEstagioMBean.obj.numeroConvenio}</b> QUE
			ENTRE SI CELEBRAM A ${ configSistema['nomeInstituicao'] } E O(A)
			${convenioEstagioMBean.obj.concedente.pessoa.nome} PARA A REALIZA��O
			DE EST�GIO CURRICULAR SUPERVISIONADO.
		</p>
	</div>

	<div class="textoConteudo">
		<p>A <b>${ configSistema['nomeInstituicao'] }</b>, Autarquia de regime
			especial, vinculada ao Minist�rio da Educa��o, com sede no endere�o
			${ configSistema['enderecoInstituicao'] }, em ${
			configSistema['cidadeInstituicao'] }/${
			configSistema['estadoInstituicao'] }, inscrita no CNPJ/MF sob o n� ${
			configSistema['cnpjInstituicao'] }, doravante denominada
			<b>${configSistema['siglaInstituicao'] }</b>, tendo como Magn�fico(a)
			Reitor(a), ${convenioEstagioMBean.reitor.nome }, portador(a) do RG n�
			${convenioEstagioMBean.reitor.identidade} e do CPF n�
			${convenioEstagioMBean.reitor.cpfCnpjFormatado} , residente e domiciliado em
			${convenioEstagioMBean.reitor.enderecoContato.municipio.nome}/${convenioEstagioMBean.reitor.enderecoContato.unidadeFederativa.sigla},
			que no uso das atribui��es que lhe confere o Estatuto da ${
			configSistema['siglaInstituicao'] } delega compet�ncia ao(�)
			Pr�-Reitor(a) de Gradua��o, ${convenioEstagioMBean.proReitor.nome },
			RG n� ${convenioEstagioMBean.proReitor.identidade}, CPF n�
			${convenioEstagioMBean.proReitor.cpfCnpjFormatado}, atrav�s da Portaria n�
			755/11-R, de 22/06/2011 para, neste ato, representar a ${
			configSistema['siglaInstituicao'] }, e o(a)
			${convenioEstagioMBean.obj.concedente.pessoa.nome}, com sede �
			${convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.tipoLogradouro.descricao}
			${convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.logradouro},
			n&deg;
			${convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.numero},
			<h:outputText value="#{ convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.complemento }, " 
				rendered="#{ not empty convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.complemento }" />
			<h:outputText value="#{ convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.bairro }, " 
				rendered="#{ not empty convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.bairro }" />
			${convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.municipio.nome}/${convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.unidadeFederativa.sigla},
			<h:outputText value="CEP: #{ convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.cep }, " 
				rendered="#{ not empty convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.cep }" />
			inscrita no CNPJ/MF sob o n&deg;
			${convenioEstagioMBean.obj.concedente.pessoa.cpfCnpjFormatado },
			doravante denominada <b>CONCEDENTE</b>, neste ato representada por seu(ua)
			${convenioEstagioMBean.obj.concedente.responsavel.cargo }, Sr(a).
			${convenioEstagioMBean.obj.concedente.responsavel.pessoa.nome},
			portador(a) do RG n&deg;
			${convenioEstagioMBean.obj.concedente.responsavel.pessoa.identidade}
			e CPF n&deg;
			${convenioEstagioMBean.obj.concedente.responsavel.pessoa.cpfCnpjFormatado},
			resolvem de comum acordo firmar o presente Conv�nio nos termos que
			disp�em a Lei Federal n� 11.788, de 25 de setembro de 2008, a
			Resolu��o n� 178/92 &ndash; CONSEPE, de 22 de setembro de 1992, a Resolu��o
			227/2009 &ndash; CONSEPE, de 03 de dezembro de 2009 e mediante as cl�usulas
			e condi��es seguintes:</p>

		<p class="clausula">CL�USULA PRIMEIRA - DO OBJETO</p>

		<p>O presente Conv�nio tem por objeto proporcionar a estudantes
			regularmente matriculados e com efetiva freq��ncia no(s) diversos
			cursos regulares da ${ configSistema['siglaInstituicao'] }, a
			realiza��o de Est�gio Curricular Obrigat�rio e N�o Obrigat�rio, de
			acordo com o projeto pedag�gico do(s) Curso(s).</p>

		<p class="subClausula"><b>SUBCL�USULA PRIMEIRA</b> - Entende-se por
			Est�gio Curricular Obrigat�rio aquele definido como tal no projeto do
			curso, cuja carga hor�ria � requisito para aprova��o e obten��o de
			diploma.</p>

		<p class="subClausula"><b>SUBCL�USULA SEGUNDA</b> - Entende-se por
			Estagio Curricular N�o Obrigat�rio aquele desenvolvido como atividade
			opcional, acrescido � carga hor�ria regular e obrigat�ria.</p>


		<p class="clausula">CL�USULA SEGUNDA - DO EST�GIO</p>

		<p>O est�gio dever� proporcionar experi�ncia pr�tica na linha de
			forma��o profissional do estudante.</p>


		<p class="subClausula"><b>SUBCL�USULA �NICA</b> - Em nenhuma hip�tese
			poder� ser cobrada qualquer taxa ao estudante.</p>

		<p class="clausula">CL�USULA TERCEIRA - DA FORMALIZA��O DO
			EST�GIO</p>

		<p>A formaliza��o da concess�o do est�gio efetivar-se-� mediante
			Termo de Compromisso do Estagi�rio (Anexo I) a ser firmado entre o
			CONCEDENTE e o ESTAGI�RIO, com a interveni�ncia obrigat�ria da ${
			configSistema['siglaInstituicao'] }, sendo necess�ria a elabora��o
			pr�via do Plano de Atividades do Estagi�rio (Anexo II).</p>

		<p class="subClausula"><b>SUBCL�USULA PRIMEIRA</b> - Por parte da ${
			configSistema['siglaInstituicao'] }, o Coordenador do Curso assinar�
			posteriormente �s demais assinaturas, as 03 (tr�s) vias, de igual
			teor e forma, do Termo de Compromisso do Estagi�rio (TCE) e do Plano
			de Atividades do Estagi�rio, ficando assim distribu�das: 01 (uma) via
			com o Estagi�rio, 01 (uma) via com a Coordena��o do Curso, 01 (uma)
			via com a parte concedente do est�gio para efeito de controle e
			acompanhamento.</p>

		<p class="subClausula"><b>SUBCL�USULA SEGUNDA</b> - No Termo de
			Compromisso do Estagi�rio dever�o estar contidas, dentre outras, as
			informa��es sobre: local de realiza��o do est�gio, dura��o do est�gio
			(in�cio e t�rmino), jornada de atividades, o Seguro Contra Acidentes
			Pessoais: (nome da Seguradora, CNPJ, n� da Ap�lice e Valor do Seguro)
			e as atividades que o estudante ir� desenvolver.</p>


		<p class="subClausula"><b>SUBCL�USULA TERCEIRA</b> - O Plano de
			Atividades do Estagi�rio (a ser incorporado ao Termo de Compromisso
			por meio de aditivo � medida que for avaliado, progressivamente, o
			desempenho do estudante) ser� elaborado conjuntamente pelas partes
			(${ configSistema['siglaInstituicao'] }, CONCEDENTE e ESTUDANTE), em
			03 (tr�s) vias de igual teor e forma, devendo ser assinado pelo
			estudante, pelo Professor Orientador e pelo Concedente e encaminhado
			ao coordenador do Curso para emiss�o do parecer.</p>

		<p class="clausula">CL�USULA QUARTA - DAS OBRIGA��ES</p>

		<p class="item">I - S�o Obriga��es da ${
			configSistema['siglaInstituicao'] } atrav�s das COORDENA��ES DE
			CURSOS:</p>

		<p class="subItem">a) Observar a rela��o existente entre o Curso e
			as atividades pr�ticas a serem desenvolvidas durante o est�gio;</p>

		<p class="subItem">b) Encaminhar ao CONCEDENTE, o estudante
			candidato ao est�gio, considerando a regularidade de sua situa��o
			acad�mica e adotando outros crit�rios julgados convenientes;</p>

		<p class="subItem">c) Participar da elabora��o do Plano de
			Atividades do Estagi�rio;</p>

		<p class="subItem">d) Firmar, na condi��o de interveniente, o
			Termo de Compromisso do Estagi�rio (TCE), zelando pelo seu
			cumprimento;</p>

		<p class="subItem">e) Acompanhar o est�gio atrav�s de relat�rios
			semestrais elaborados pelo Estagi�rio e pelo CONCEDENTE;</p>

		<p class="subItem">f) Indicar um Professor-Orientador, da �rea a
			ser desenvolvida no est�gio, para elaborar em conjunto com o
			estudante e o concedente, o Plano de Atividades do Estagi�rio, bem
			como responsabilizar-se pelo acompanhamento e avalia��o das
			atividades do Estagi�rio, devendo para isso, solicitar a participa��o
			do CONCEDENTE;</p>

		<p class="subItem">g) Comunicar ao CONCEDENTE quando o Estagi�rio
			concluir ou interromper seu curso e/ou qualquer ocorr�ncia que possa
			interferir na execu��o deste Conv�nio;</p>

		<p class="subItem">h) Indicar, quando da celebra��o do Termo de
			Compromisso do Estagi�rio, as condi��es de adequa��o do est�gio �
			proposta pedag�gica do curso, � etapa e modalidade da forma��o
			escolar do estudante e ao hor�rio e calend�rio escolar;</p>

		<p class="subItem">i) Avaliar as instala��es da parte concedente
			do est�gio e sua adequa��o � forma��o cultural e profissional do
			educando;</p>

		<p class="subItem">j) Elaborar normas complementares e
			instrumentos de avalia��o dos est�gios de seus educandos.</p>

		<p class="item">II - S�o Obriga��es do CONCEDENTE:</p>

		<p class="subItem">a) Ofertar instala��es que tenham condi��es de
			proporcionar ao estudante atividades de aprendizagem social,
			profissional e cultural;</p>

		<p class="subItem">b) Selecionar e receber o estudante para
			est�gio oferecendo-lhe condi��es para o exerc�cio de atividades
			pr�ticas relacionadas � sua �rea de forma��o acad�mica e
			profissional;</p>

		<p class="subItem">c) Elaborar o Plano de Atividades do
			Estagi�rio;</p>

		<p class="subItem">d) Firmar com o estudante o Termo de
			Compromisso do Estagi�rio (TCE), zelando pelo seu cumprimento;</p>

		<p class="subItem">e) Compatibilizar as atividades a serem
			desenvolvidas no est�gio com aquelas constantes no Termo de
			Compromisso do Estagi�rio;</p>

		<p class="subItem">f) Indicar funcion�rio do seu quadro de
			pessoal, com forma��o ou experi�ncia profissional na �rea de
			conhecimento desenvolvida no curso do Estagi�rio para orientar e
			supervisionar at� 10 (dez) Estagi�rios simultaneamente;</p>

		<p class="subItem">g) Participar conjuntamente com o Professor
			Orientador quando da avalia��o do Estagi�rio;</p>

		<p class="subItem">h) Enviar � institui��o de ensino, com
			periodicidade m�nima de 06 (seis) meses, relat�rio de atividades, com
			vista obrigat�ria ao Estagi�rio;</p>

		<p class="subItem">i) Comunicar � ${
			configSistema['siglaInstituicao'] }/Coordena��o de Curso qualquer
			ocorr�ncia que possa interferir na execu��o deste Conv�nio;</p>


		<p class="subItem">j) Aplicar ao Estagi�rio a legisla��o
			relacionada � sa�de e � seguran�a no trabalho;</p>

		<p class="subItem">k) Manter a disposi��o da fiscaliza��o
			documentos que comprovem a rela��o de est�gio;</p>

		<p class="subItem">l) Por ocasi�o do desligamento do estagi�rio,
			dever� ser entregue termo de realiza��o do est�gio com indica��o
			resumida das atividades desenvolvidas, dos per�odos e da avalia��o de
			desempenho;</p>

		<p class="subItem">m) Dever� ser enviado Relat�rio Final de
			Atividades � Institui��o de Ensino, com vista obrigat�ria ao
			estagi�rio.</p>

		<p class="clausula">CL�USULA QUINTA - DA DURA��O DO EST�GIO E DA
			JORNADA DE ATIVIDADES.</p>

		<p>A dura��o do est�gio observar� o limite m�nimo de 01 (um)
			semestre letivo at� o limite m�ximo de 04 (quatro) semestres letivos,
			limitados a 02 (dois) anos, devendo constar no Termo de Compromisso
			do Estagi�rio o per�odo de in�cio e t�rmino do est�gio.</p>

		<p class="subClausula"><b>SUBCL�USULA PRIMEIRA</b> - Nos casos de Est�gio
			Curricular Obrigat�rio, a dura��o do est�gio corresponder� ao
			cumprimento da carga hor�ria estabelecida pela disciplina de est�gio,
			devendo constar no Termo de Compromisso do Est�gio o per�odo de
			in�cio e t�rmino do est�gio.</p>

		<p class="subClausula"><b>SUBCL�USULA SEGUNDA</b> - A Jornada de
			Atividades ser� definida de comum acordo entre a ${
			configSistema['siglaInstituicao'] }, o CONCEDENTE e o ESTUDANTE,
			devendo constar no Termo de Compromisso e ser compat�vel com as
			atividades escolares, n�o devendo ultrapassar 06 (seis) horas di�rias
			e 30 (trinta) horas semanais.</p>

		<p class="subClausula"><b>SUBCL�USULA TERCEIRA</b> - O est�gio relativo a
			cursos que alternam teoria e pr�tica, nos per�odos em que n�o est�o
			programadas aulas presenciais, poder� ter jornada de at� 40
			(quarenta) horas semanais, desde que isso esteja previsto no projeto
			pedag�gico do curso e da institui��o de ensino.</p>


		<p class="clausula">CL�USULA SEXTA - DA BOLSA DE EST�GIO</p>

		<p>Nos casos de Est�gio Curricular N�o Obrigat�rio, O CONCEDENTE
			dever� efetuar, mensalmente uma retribui��o financeira ao Estagi�rio,
			a t�tulo de bolsa, fazendo constar o seu valor no Termo de
			Compromisso do Estagi�rio, bem como o valor do Aux�lio-transporte.</p>


		<p class="subClausula"><b>SUBCL�USULA PRIMEIRA</b> - � facultado o
			pagamento de bolsa de est�gio e aux�lio transporte, quando se tratar
			da modalidade de Est�gio Curricular Obrigat�rio.</p>

		<p class="subClausula"><b>SUBCL�USULA SEGUNDA</b> - Para efeito de
			c�lculo do pagamento da bolsa, o Concedente dever� considerar o
			disposto no � 1� do Art. 14 da Orienta��o Normativa n� 7 de 30 de
			outubro de 2008, al�m da freq��ncia mensal, deduzindo-se os dias de
			falta n�o justificadas, salvo na hip�tese de compensa��o de hor�rio.
		</p>

		<p class="clausula">CL�USULA S�TIMA - DO RECESSO</p>

		<p>Sempre que o est�gio tenha dura��o igual ou superior a 01 (um)
			ano, ser� assegurado ao Estagi�rio per�odo de recesso de 30 (trinta)
			dias, a ser gozado preferencialmente durante suas f�rias escolares,
			devendo ser remunerado, quando se tratar de Est�gio Remunerado.</p>

		<p class="subClausula"><b>SUBCL�USULA �NICA</b> - Os dias de recesso
			previstos no "caput" desta Cl�usula ser�o concedidos de maneira
			proporcional, no caso do est�gio ter dura��o inferior a 01 (um) ano.</p>

		<p class="clausula">CL�USULA OITAVA - DO V�NCULO EMPREGAT�CIO.</p>

		<p>A realiza��o do est�gio n�o acarretar� v�nculo empregat�cio de
			qualquer natureza, desde que respeitado o Art. 3� da Lei 11.788/08.</p>


		<p class="clausula">CL�USULA NONA - DO SEGURO OBRIGAT�RIO</p>

		<p>Nos casos de Est�gio Curricular N�o Obrigat�rio, o CONCEDENTE
			se compromete a fazer para cada Estagi�rio, durante o per�odo de
			est�gio, um Seguro Contra Acidentes Pessoais, fazendo constar o nome
			da Seguradora, CNPJ, n� da Ap�lice e o valor do seguro no Termo de
			Compromisso do Estagi�rio.</p>

		<p class="subClausula"><b>SUBCL�USULA �NICA</b> - A ${configSistema['siglaInstituicao'] } 
		providenciar� o Seguro Contra
			Acidentes Pessoais em casos de Est�gio Curricular Obrigat�rio.</p>


		<p class="clausula">CL�USULA D�CIMA - DO DESLIGAMENTO DO
			ESTAGI�RIO</p>

		<p class="subItem">a) Automaticamente, quando do t�rmino do
			Est�gio;</p>

		<p class="subItem">b) A qualquer tempo, no interesse ou
			conveni�ncia do CONCEDENTE e/ou da ${
			configSistema['siglaInstituicao'] };</p>

		<p class="subItem">c) A seu pedido;</p>

		<p class="subItem">d) Por descumprimento de cl�usula do Termo de
			Compromisso;</p>

		<p class="subItem">e) Quando houver conclus�o ou interrup��o do
			curso;</p>

		<p class="subItem">f) Depois de decorrida a ter�a parte do tempo
			previsto para a dura��o do est�gio, se comprovada a insufici�ncia na
			avalia��o de desempenho no �rg�o ou entidade ou na institui��o de
			ensino;</p>


		<p class="subItem">g) Pelo n�o comparecimento, sem motivo
			justificado, por mais de cinco dias, consecutivos ou n�o, no per�odo
			de um m�s, ou por trinta dias durante todo o per�odo do est�gio</p>

		<p class="subClausula"><b>SUBCL�USULA PRIMEIRA</b> - Por ocasi�o do
			desligamento do Estagi�rio, o Concedente dever� entregar Termo de
			Realiza��o do Est�gio com indica��o resumida das atividades
			desenvolvidas, dos per�odos e da avalia��o de desempenho.</p>

		<p class="clausula">CL�USULA D�CIMA PRIMEIRA - DA VIG�NCIA</p>

		<p>O presente Termo de Conv�nio vigorar� por 05 (cinco) anos, a
			partir da data de sua assinatura, podendo ser alterado mediante Termo
			Aditivo firmado pelas partes.</p>

		<p class="clausula">CL�USULA D�CIMA SEGUNDA - DO IN�CIO DO EST�GIO</p>

		<p>Nos termos da Lei 11.788/08, n�o poder� ocorrer o in�cio
			efetivo do est�gio antes que o Termo de Compromisso de Est�gio seja
			assinado por todos os signat�rios indispens�veis.</p>

		<p class="clausula">CL�USULA D�CIMA TERCEIRA - DA DEN�NCIA E DA
			RESCIS�O</p>


		<p>Este Termo poder� ser denunciado ou rescindido por qualquer das
			partes, em qualquer tempo, desde que aquela que assim o desejar
			comunique a outra, por escrito, com anteced�ncia m�nima de 15
			(quinze) dias, sem preju�zo das atividades em andamento.</p>

		<p class="clausula">CL�USULA D�CIMA QUARTA - DO FORO</p>

		<p>Para dirimir quaisquer d�vidas ou controv�rsias decorrentes do
			presente Conv�nio, que n�o puderem ser resolvidas amigavelmente pelas
			partes, fica eleito o Foro da Justi�a Federal de Primeiro Grau no Rio
			Grande do Norte, com ren�ncia a qualquer outro por mais privilegiado
			que seja.</p>

		<p>E, por estarem assim ajustados, assinam o presente Instrumento
			em 02 (duas) vias de igual teor e forma para fins de direito, na
			presen�a das testemunhas abaixo arroladas.</p>

		<br/><br/><br/>

		<p style="text-align: center";>
			${ configSistema['cidadeInstituicao'] },
			<ufrn:format type="dia_mes_ano" valor="${convenioEstagioMBean.obj.dataAnalise}" />
		</p>
	</div>

		<br/><br/><br/>
		<center>
		<table width="100%" border="0">
			<tr>
				<td style="text-align: center;">_______________________________________</td>
				<td width="10%"></td>
				<td style="text-align: center;">_______________________________________</td>
			</tr>
			<tr>
				<td style="text-align: center;">
					<b>${convenioEstagioMBean.proReitor.nome}</b><br/>
					<span style="size: 0.8 em;">Pr�-Reitor de Gradua��o - ${configSistema['siglaInstituicao'] }</span>
				</td>
				<td></td>
				<td style="text-align: center;">
					<b>${convenioEstagioMBean.obj.concedente.responsavel.pessoa.nome}</b><br/>
					<span style="size: 0.8 em;">${convenioEstagioMBean.obj.concedente.responsavel.cargo}</span>
				</td>
			</tr>
		</table>
	</center>
	<br/><br/><br/>
		<p>TESTEMUNHAS:</p>
	<br/><br/>
		<table width="100%" border="0">
			<tr>
				<td style="text-align: center;">_______________________________________</td>
				<td width="10%"></td>
				<td style="text-align: center;">_______________________________________</td>
			</tr>
			<tr>
				<td style="text-align: left;">
					NOME:<br/>
					CPF:
				</td>
				<td></td>
				<td style="text-align: left;">
					NOME:<br/>
					CPF:
				</td>
			</tr>
		</table>

		<br />
		<br />
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
