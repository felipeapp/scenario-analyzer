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
			CONVÊNIO <b>N&deg; ${convenioEstagioMBean.obj.numeroConvenio}</b> QUE
			ENTRE SI CELEBRAM A ${ configSistema['nomeInstituicao'] } E O(A)
			${convenioEstagioMBean.obj.concedente.pessoa.nome} PARA A REALIZAÇÃO
			DE ESTÁGIO CURRICULAR SUPERVISIONADO.
		</p>
	</div>

	<div class="textoConteudo">
		<p>A <b>${ configSistema['nomeInstituicao'] }</b>, Autarquia de regime
			especial, vinculada ao Ministério da Educação, com sede no endereço
			${ configSistema['enderecoInstituicao'] }, em ${
			configSistema['cidadeInstituicao'] }/${
			configSistema['estadoInstituicao'] }, inscrita no CNPJ/MF sob o nº ${
			configSistema['cnpjInstituicao'] }, doravante denominada
			<b>${configSistema['siglaInstituicao'] }</b>, tendo como Magnífico(a)
			Reitor(a), ${convenioEstagioMBean.reitor.nome }, portador(a) do RG nº
			${convenioEstagioMBean.reitor.identidade} e do CPF nº
			${convenioEstagioMBean.reitor.cpfCnpjFormatado} , residente e domiciliado em
			${convenioEstagioMBean.reitor.enderecoContato.municipio.nome}/${convenioEstagioMBean.reitor.enderecoContato.unidadeFederativa.sigla},
			que no uso das atribuições que lhe confere o Estatuto da ${
			configSistema['siglaInstituicao'] } delega competência ao(à)
			Pró-Reitor(a) de Graduação, ${convenioEstagioMBean.proReitor.nome },
			RG nº ${convenioEstagioMBean.proReitor.identidade}, CPF nº
			${convenioEstagioMBean.proReitor.cpfCnpjFormatado}, através da Portaria nº
			755/11-R, de 22/06/2011 para, neste ato, representar a ${
			configSistema['siglaInstituicao'] }, e o(a)
			${convenioEstagioMBean.obj.concedente.pessoa.nome}, com sede à
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
			resolvem de comum acordo firmar o presente Convênio nos termos que
			dispõem a Lei Federal nº 11.788, de 25 de setembro de 2008, a
			Resolução nº 178/92 &ndash; CONSEPE, de 22 de setembro de 1992, a Resolução
			227/2009 &ndash; CONSEPE, de 03 de dezembro de 2009 e mediante as cláusulas
			e condições seguintes:</p>

		<p class="clausula">CLÁUSULA PRIMEIRA - DO OBJETO</p>

		<p>O presente Convênio tem por objeto proporcionar a estudantes
			regularmente matriculados e com efetiva freqüência no(s) diversos
			cursos regulares da ${ configSistema['siglaInstituicao'] }, a
			realização de Estágio Curricular Obrigatório e Não Obrigatório, de
			acordo com o projeto pedagógico do(s) Curso(s).</p>

		<p class="subClausula"><b>SUBCLÁUSULA PRIMEIRA</b> - Entende-se por
			Estágio Curricular Obrigatório aquele definido como tal no projeto do
			curso, cuja carga horária é requisito para aprovação e obtenção de
			diploma.</p>

		<p class="subClausula"><b>SUBCLÁUSULA SEGUNDA</b> - Entende-se por
			Estagio Curricular Não Obrigatório aquele desenvolvido como atividade
			opcional, acrescido à carga horária regular e obrigatória.</p>


		<p class="clausula">CLÁUSULA SEGUNDA - DO ESTÁGIO</p>

		<p>O estágio deverá proporcionar experiência prática na linha de
			formação profissional do estudante.</p>


		<p class="subClausula"><b>SUBCLÁUSULA ÚNICA</b> - Em nenhuma hipótese
			poderá ser cobrada qualquer taxa ao estudante.</p>

		<p class="clausula">CLÁUSULA TERCEIRA - DA FORMALIZAÇÃO DO
			ESTÁGIO</p>

		<p>A formalização da concessão do estágio efetivar-se-á mediante
			Termo de Compromisso do Estagiário (Anexo I) a ser firmado entre o
			CONCEDENTE e o ESTAGIÁRIO, com a interveniência obrigatória da ${
			configSistema['siglaInstituicao'] }, sendo necessária a elaboração
			prévia do Plano de Atividades do Estagiário (Anexo II).</p>

		<p class="subClausula"><b>SUBCLÁUSULA PRIMEIRA</b> - Por parte da ${
			configSistema['siglaInstituicao'] }, o Coordenador do Curso assinará
			posteriormente às demais assinaturas, as 03 (três) vias, de igual
			teor e forma, do Termo de Compromisso do Estagiário (TCE) e do Plano
			de Atividades do Estagiário, ficando assim distribuídas: 01 (uma) via
			com o Estagiário, 01 (uma) via com a Coordenação do Curso, 01 (uma)
			via com a parte concedente do estágio para efeito de controle e
			acompanhamento.</p>

		<p class="subClausula"><b>SUBCLÁUSULA SEGUNDA</b> - No Termo de
			Compromisso do Estagiário deverão estar contidas, dentre outras, as
			informações sobre: local de realização do estágio, duração do estágio
			(início e término), jornada de atividades, o Seguro Contra Acidentes
			Pessoais: (nome da Seguradora, CNPJ, nº da Apólice e Valor do Seguro)
			e as atividades que o estudante irá desenvolver.</p>


		<p class="subClausula"><b>SUBCLÁUSULA TERCEIRA</b> - O Plano de
			Atividades do Estagiário (a ser incorporado ao Termo de Compromisso
			por meio de aditivo à medida que for avaliado, progressivamente, o
			desempenho do estudante) será elaborado conjuntamente pelas partes
			(${ configSistema['siglaInstituicao'] }, CONCEDENTE e ESTUDANTE), em
			03 (três) vias de igual teor e forma, devendo ser assinado pelo
			estudante, pelo Professor Orientador e pelo Concedente e encaminhado
			ao coordenador do Curso para emissão do parecer.</p>

		<p class="clausula">CLÁUSULA QUARTA - DAS OBRIGAÇÕES</p>

		<p class="item">I - São Obrigações da ${
			configSistema['siglaInstituicao'] } através das COORDENAÇÕES DE
			CURSOS:</p>

		<p class="subItem">a) Observar a relação existente entre o Curso e
			as atividades práticas a serem desenvolvidas durante o estágio;</p>

		<p class="subItem">b) Encaminhar ao CONCEDENTE, o estudante
			candidato ao estágio, considerando a regularidade de sua situação
			acadêmica e adotando outros critérios julgados convenientes;</p>

		<p class="subItem">c) Participar da elaboração do Plano de
			Atividades do Estagiário;</p>

		<p class="subItem">d) Firmar, na condição de interveniente, o
			Termo de Compromisso do Estagiário (TCE), zelando pelo seu
			cumprimento;</p>

		<p class="subItem">e) Acompanhar o estágio através de relatórios
			semestrais elaborados pelo Estagiário e pelo CONCEDENTE;</p>

		<p class="subItem">f) Indicar um Professor-Orientador, da área a
			ser desenvolvida no estágio, para elaborar em conjunto com o
			estudante e o concedente, o Plano de Atividades do Estagiário, bem
			como responsabilizar-se pelo acompanhamento e avaliação das
			atividades do Estagiário, devendo para isso, solicitar a participação
			do CONCEDENTE;</p>

		<p class="subItem">g) Comunicar ao CONCEDENTE quando o Estagiário
			concluir ou interromper seu curso e/ou qualquer ocorrência que possa
			interferir na execução deste Convênio;</p>

		<p class="subItem">h) Indicar, quando da celebração do Termo de
			Compromisso do Estagiário, as condições de adequação do estágio à
			proposta pedagógica do curso, à etapa e modalidade da formação
			escolar do estudante e ao horário e calendário escolar;</p>

		<p class="subItem">i) Avaliar as instalações da parte concedente
			do estágio e sua adequação à formação cultural e profissional do
			educando;</p>

		<p class="subItem">j) Elaborar normas complementares e
			instrumentos de avaliação dos estágios de seus educandos.</p>

		<p class="item">II - São Obrigações do CONCEDENTE:</p>

		<p class="subItem">a) Ofertar instalações que tenham condições de
			proporcionar ao estudante atividades de aprendizagem social,
			profissional e cultural;</p>

		<p class="subItem">b) Selecionar e receber o estudante para
			estágio oferecendo-lhe condições para o exercício de atividades
			práticas relacionadas à sua área de formação acadêmica e
			profissional;</p>

		<p class="subItem">c) Elaborar o Plano de Atividades do
			Estagiário;</p>

		<p class="subItem">d) Firmar com o estudante o Termo de
			Compromisso do Estagiário (TCE), zelando pelo seu cumprimento;</p>

		<p class="subItem">e) Compatibilizar as atividades a serem
			desenvolvidas no estágio com aquelas constantes no Termo de
			Compromisso do Estagiário;</p>

		<p class="subItem">f) Indicar funcionário do seu quadro de
			pessoal, com formação ou experiência profissional na área de
			conhecimento desenvolvida no curso do Estagiário para orientar e
			supervisionar até 10 (dez) Estagiários simultaneamente;</p>

		<p class="subItem">g) Participar conjuntamente com o Professor
			Orientador quando da avaliação do Estagiário;</p>

		<p class="subItem">h) Enviar à instituição de ensino, com
			periodicidade mínima de 06 (seis) meses, relatório de atividades, com
			vista obrigatória ao Estagiário;</p>

		<p class="subItem">i) Comunicar à ${
			configSistema['siglaInstituicao'] }/Coordenação de Curso qualquer
			ocorrência que possa interferir na execução deste Convênio;</p>


		<p class="subItem">j) Aplicar ao Estagiário a legislação
			relacionada à saúde e à segurança no trabalho;</p>

		<p class="subItem">k) Manter a disposição da fiscalização
			documentos que comprovem a relação de estágio;</p>

		<p class="subItem">l) Por ocasião do desligamento do estagiário,
			deverá ser entregue termo de realização do estágio com indicação
			resumida das atividades desenvolvidas, dos períodos e da avaliação de
			desempenho;</p>

		<p class="subItem">m) Deverá ser enviado Relatório Final de
			Atividades à Instituição de Ensino, com vista obrigatória ao
			estagiário.</p>

		<p class="clausula">CLÁUSULA QUINTA - DA DURAÇÃO DO ESTÁGIO E DA
			JORNADA DE ATIVIDADES.</p>

		<p>A duração do estágio observará o limite mínimo de 01 (um)
			semestre letivo até o limite máximo de 04 (quatro) semestres letivos,
			limitados a 02 (dois) anos, devendo constar no Termo de Compromisso
			do Estagiário o período de início e término do estágio.</p>

		<p class="subClausula"><b>SUBCLÁUSULA PRIMEIRA</b> - Nos casos de Estágio
			Curricular Obrigatório, a duração do estágio corresponderá ao
			cumprimento da carga horária estabelecida pela disciplina de estágio,
			devendo constar no Termo de Compromisso do Estágio o período de
			início e término do estágio.</p>

		<p class="subClausula"><b>SUBCLÁUSULA SEGUNDA</b> - A Jornada de
			Atividades será definida de comum acordo entre a ${
			configSistema['siglaInstituicao'] }, o CONCEDENTE e o ESTUDANTE,
			devendo constar no Termo de Compromisso e ser compatível com as
			atividades escolares, não devendo ultrapassar 06 (seis) horas diárias
			e 30 (trinta) horas semanais.</p>

		<p class="subClausula"><b>SUBCLÁUSULA TERCEIRA</b> - O estágio relativo a
			cursos que alternam teoria e prática, nos períodos em que não estão
			programadas aulas presenciais, poderá ter jornada de até 40
			(quarenta) horas semanais, desde que isso esteja previsto no projeto
			pedagógico do curso e da instituição de ensino.</p>


		<p class="clausula">CLÁUSULA SEXTA - DA BOLSA DE ESTÁGIO</p>

		<p>Nos casos de Estágio Curricular Não Obrigatório, O CONCEDENTE
			deverá efetuar, mensalmente uma retribuição financeira ao Estagiário,
			a título de bolsa, fazendo constar o seu valor no Termo de
			Compromisso do Estagiário, bem como o valor do Auxílio-transporte.</p>


		<p class="subClausula"><b>SUBCLÁUSULA PRIMEIRA</b> - É facultado o
			pagamento de bolsa de estágio e auxílio transporte, quando se tratar
			da modalidade de Estágio Curricular Obrigatório.</p>

		<p class="subClausula"><b>SUBCLÁUSULA SEGUNDA</b> - Para efeito de
			cálculo do pagamento da bolsa, o Concedente deverá considerar o
			disposto no § 1º do Art. 14 da Orientação Normativa nº 7 de 30 de
			outubro de 2008, além da freqüência mensal, deduzindo-se os dias de
			falta não justificadas, salvo na hipótese de compensação de horário.
		</p>

		<p class="clausula">CLÁUSULA SÉTIMA - DO RECESSO</p>

		<p>Sempre que o estágio tenha duração igual ou superior a 01 (um)
			ano, será assegurado ao Estagiário período de recesso de 30 (trinta)
			dias, a ser gozado preferencialmente durante suas férias escolares,
			devendo ser remunerado, quando se tratar de Estágio Remunerado.</p>

		<p class="subClausula"><b>SUBCLÁUSULA ÚNICA</b> - Os dias de recesso
			previstos no "caput" desta Cláusula serão concedidos de maneira
			proporcional, no caso do estágio ter duração inferior a 01 (um) ano.</p>

		<p class="clausula">CLÁUSULA OITAVA - DO VÍNCULO EMPREGATÍCIO.</p>

		<p>A realização do estágio não acarretará vínculo empregatício de
			qualquer natureza, desde que respeitado o Art. 3º da Lei 11.788/08.</p>


		<p class="clausula">CLÁUSULA NONA - DO SEGURO OBRIGATÓRIO</p>

		<p>Nos casos de Estágio Curricular Não Obrigatório, o CONCEDENTE
			se compromete a fazer para cada Estagiário, durante o período de
			estágio, um Seguro Contra Acidentes Pessoais, fazendo constar o nome
			da Seguradora, CNPJ, nº da Apólice e o valor do seguro no Termo de
			Compromisso do Estagiário.</p>

		<p class="subClausula"><b>SUBCLÁUSULA ÚNICA</b> - A ${configSistema['siglaInstituicao'] } 
		providenciará o Seguro Contra
			Acidentes Pessoais em casos de Estágio Curricular Obrigatório.</p>


		<p class="clausula">CLÁUSULA DÉCIMA - DO DESLIGAMENTO DO
			ESTAGIÁRIO</p>

		<p class="subItem">a) Automaticamente, quando do término do
			Estágio;</p>

		<p class="subItem">b) A qualquer tempo, no interesse ou
			conveniência do CONCEDENTE e/ou da ${
			configSistema['siglaInstituicao'] };</p>

		<p class="subItem">c) A seu pedido;</p>

		<p class="subItem">d) Por descumprimento de cláusula do Termo de
			Compromisso;</p>

		<p class="subItem">e) Quando houver conclusão ou interrupção do
			curso;</p>

		<p class="subItem">f) Depois de decorrida a terça parte do tempo
			previsto para a duração do estágio, se comprovada a insuficiência na
			avaliação de desempenho no órgão ou entidade ou na instituição de
			ensino;</p>


		<p class="subItem">g) Pelo não comparecimento, sem motivo
			justificado, por mais de cinco dias, consecutivos ou não, no período
			de um mês, ou por trinta dias durante todo o período do estágio</p>

		<p class="subClausula"><b>SUBCLÁUSULA PRIMEIRA</b> - Por ocasião do
			desligamento do Estagiário, o Concedente deverá entregar Termo de
			Realização do Estágio com indicação resumida das atividades
			desenvolvidas, dos períodos e da avaliação de desempenho.</p>

		<p class="clausula">CLÁUSULA DÉCIMA PRIMEIRA - DA VIGÊNCIA</p>

		<p>O presente Termo de Convênio vigorará por 05 (cinco) anos, a
			partir da data de sua assinatura, podendo ser alterado mediante Termo
			Aditivo firmado pelas partes.</p>

		<p class="clausula">CLÁUSULA DÉCIMA SEGUNDA - DO INÍCIO DO ESTÁGIO</p>

		<p>Nos termos da Lei 11.788/08, não poderá ocorrer o início
			efetivo do estágio antes que o Termo de Compromisso de Estágio seja
			assinado por todos os signatários indispensáveis.</p>

		<p class="clausula">CLÁUSULA DÉCIMA TERCEIRA - DA DENÚNCIA E DA
			RESCISÃO</p>


		<p>Este Termo poderá ser denunciado ou rescindido por qualquer das
			partes, em qualquer tempo, desde que aquela que assim o desejar
			comunique a outra, por escrito, com antecedência mínima de 15
			(quinze) dias, sem prejuízo das atividades em andamento.</p>

		<p class="clausula">CLÁUSULA DÉCIMA QUARTA - DO FORO</p>

		<p>Para dirimir quaisquer dúvidas ou controvérsias decorrentes do
			presente Convênio, que não puderem ser resolvidas amigavelmente pelas
			partes, fica eleito o Foro da Justiça Federal de Primeiro Grau no Rio
			Grande do Norte, com renúncia a qualquer outro por mais privilegiado
			que seja.</p>

		<p>E, por estarem assim ajustados, assinam o presente Instrumento
			em 02 (duas) vias de igual teor e forma para fins de direito, na
			presença das testemunhas abaixo arroladas.</p>

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
					<span style="size: 0.8 em;">Pró-Reitor de Graduação - ${configSistema['siglaInstituicao'] }</span>
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
