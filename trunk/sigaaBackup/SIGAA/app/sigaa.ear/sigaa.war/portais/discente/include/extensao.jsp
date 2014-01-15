    <ul>
		 <li><h:commandLink action="#{ atividadeExtensao.preLocalizar }" value="Consultar Ações"  /> </li>
		 <li><h:commandLink action="#{ planoTrabalhoExtensao.carregarPlanosTrabalhoDiscenteLogado }" value="Meus Planos de Trabalho"  /> </li>
		 <li><h:commandLink action="#{ atividadeExtensao.carregarAcoesDiscenteLogado }" value="Minhas Ações como Membro da Equipe"  /> </li>
		 <li><h:commandLink action="#{ relatorioBolsistaExtensao.iniciarCadastroRelatorio }" value="Meus Relatórios"  /> </li>
		 <li><h:commandLink action="#{ documentosAutenticadosExtensao.participacoesDiscenteUsuarioLogado }" value="Certificados e Declarações"  /> </li>
		 <li><a href="${ctx}/public/extensao/consulta_inscricoes.jsf?aba=extensao">Inscrição On-line em Ações de Extensão</a></li>
		 <li><h:commandLink action="#{ selecaoDiscenteExtensao.iniciarVisualizarResultados }" value="Visualizar Resultados das inscrições"  /> </li>
    </ul>